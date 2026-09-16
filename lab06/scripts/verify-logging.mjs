import assert from 'node:assert/strict';
import { spawnSync } from 'node:child_process';
import { readFileSync, writeFileSync, mkdirSync, copyFileSync } from 'node:fs';
import { fileURLToPath } from 'node:url';
import path from 'node:path';

const root = fileURLToPath(new URL('../', import.meta.url));
const evidence = path.join(root, 'target', 'verification');
const appLog = path.join(root, 'logs', 'App', 'log4j', 'log.out');
mkdirSync(evidence, { recursive: true });

function run(name, profile, level, startupOnly = false) {
    const args = ['-B', '-ntp', '-DskipTests', '-P' + profile,
        '-Dlab.log.level=' + level, '-Dexec.args=' + (startupOnly ? 'startup-only' : ''),
        'package', 'exec:java'];
    const result = process.platform === 'win32'
        ? spawnSync('cmd.exe', ['/d', '/s', '/c', 'mvn ' + args.join(' ')],
            { cwd: root, encoding: 'utf8' })
        : spawnSync('mvn', args, { cwd: root, encoding: 'utf8' });
    const output = (result.stdout ?? '') + (result.stderr ?? '');
    writeFileSync(path.join(evidence, name + '.log'), output);
    assert.equal(result.status, 0, name + ' failed; inspect target/verification/' + name + '.log');
    assert.ok(!output.includes('multiple SLF4J providers'), 'Multiple providers active');
    return output;
}

const missing = run('no-provider', 'no-provider', 'INFO', true);
assert.match(missing, /No SLF4J providers were found/);
assert.ok(!missing.includes('Application is starting...'));
console.log('PASS: no provider emits warning and suppresses application logs.');

const logback = run('logback', 'logback', 'INFO', true);
assert.match(logback, /Application is starting/);
assert.match(logback, /Application ends/);
assert.ok(!logback.includes('No SLF4J providers'));
console.log('PASS: Logback displays lifecycle messages.');

run('startup-error', 'log4j', 'ERROR', true);
assert.equal(readFileSync(appLog, 'utf8').trim(), '');
console.log('PASS: ERROR suppresses lifecycle-only messages.');
run('startup-info', 'log4j', 'INFO', true);
assert.match(readFileSync(appLog, 'utf8'), /INFO \[.+\] .+ - Application is starting/);
console.log('PASS: INFO writes formatted lifecycle messages to the file.');

const expected = {
    ERROR: { INFO: 0, DEBUG: 0, WARN: 0, ERROR: 2 },
    WARN: { INFO: 0, DEBUG: 0, WARN: 1, ERROR: 2 },
    INFO: { INFO: 2, DEBUG: 0, WARN: 1, ERROR: 2 },
    DEBUG: { INFO: 2, DEBUG: 3, WARN: 1, ERROR: 2 }
};
for (const [level, counts] of Object.entries(expected)) {
    run('full-' + level, 'log4j', level);
    const log = readFileSync(appLog, 'utf8');
    for (const [severity, count] of Object.entries(counts)) {
        const actual = log.split(/\r?\n/).filter(line => line.includes(' ' + severity + ' [')).length;
        assert.equal(actual, count, level + ': unexpected ' + severity + ' count');
    }
    assert.match(log, /InsufficientFundsException: Insufficient funds/);
    assert.match(log, /IllegalArgumentException: Amount must be finite/);
    copyFileSync(appLog, path.join(evidence, 'full-' + level + '-application.log'));
    console.log('PASS: ' + level + ' filtering and both exception stack traces.');
}
run('final-info', 'log4j', 'INFO');
console.log('ALL LOGGING CHECKS PASSED. Restored the default INFO run.');
