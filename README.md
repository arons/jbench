# jbench
Jbench is a simple benchmark tool written in java.

### download latest version

as binaries:
```
curl -L  -o jbench  'https://github.com/arons/jbench/releases/download/v_1.0/jbench' && chmod 775 jbench
```

as jar:
```
curl -L  -o jbench.jar  'https://github.com/arons/jbench/releases/download/v_1.0/jbench.jar'
```
Note: in case you want o use the jar, for postgres test, you need to add the postgresql jdbc jar in your classpath.


### help
```
$ ./jbench -h
jbench  1.0
Usage: jbench (info,io) 
commands:
 info :  print some system info
 io   :  performs file system read/write test
 pg   :  performs postgres tests
```

### get system info
```
$ ./jbench info
```

```
jbench  1.0
Name: Linux
Version: 6.5.0-35-generic
Arch: amd64

Java version: 21.0.1
Java vendor: Oracle Corporation
Java home: null

Host jvm processors: 8 
Free mem (MB): 12677 max mem (MB): 12678 
Total mem for JVM (MB): 12678 Available processors (cores): 8

File system root: /
Total space (MB): 959824
Free space (MB): 826978
Usable space (MB): 778150
```


### test local disk io
```
$ ./jbench io -h
jbench  1.0
jbench io
options:
 block_size=8 (kb)
 numOfBlocks=256
```

```
jbench  1.0
Sun Jun 16 10:57:56 CEST 2024
Host jvm processors: 8 
Total mem for JVM (MB): 12678 
Start IO test
 block_size=8 (kb)
 numOfBlocks=262144
 fileSize=2048 mb
Location: /home/arons/gitreps/jbench/src/script/.
Total size : 959824 mb
Usable : 778150 mb
Free : 826978 mb
+++ writring test
write IO is 935.079630643966 MB/s
2048MB written in 2.19 sec
+++  reading test
read IO is 2545.312961969098 MB/s
2048MB written in 0.8 sec
```


### postgres tests

```
$ ./jbench pg -h
jbench  1.0
jbench pg url user password (help|prepare|test|clean|params)

```

```
$ ./jbench pg jdbc:postgresql://localhost:5432/postgres arons nopw  params

jbench  1.0
Host jvm processors: 8 
Total mem for JVM (MB): 12678 
Start: Sun Jun 16 11:02:13 CEST 2024
ParameterCheck start Sun Jun 16 11:02:13 CEST 2024
Version: PostgreSQL 17beta1 arons build on x86_64-pc-linux-gnu, compiled by gcc (Ubuntu 11.4.0-1ubuntu1~22.04) 11.4.0, 64-bit
Parameters:
            checkpoint_completion_target |        0.9 | Time spent flushing dirty buffers during checkpoint, as fraction of checkpoint interval.
                      checkpoint_timeout |        300 | Sets the maximum time between automatic WAL checkpoints.
               default_toast_compression |       pglz | Sets the default compression method for compressible values.
                    effective_cache_size |     524288 | Sets the planner's assumption about the total size of the data caches.
                    maintenance_work_mem |      65536 | Sets the maximum memory to be used for maintenance operations.
                         max_connections |        100 | Sets the maximum number of concurrent connections.
               max_locks_per_transaction |         64 | Sets the maximum number of locks per transaction.
                    max_parallel_workers |          8 | Sets the maximum number of parallel workers that can be active at one time.
         max_parallel_workers_per_gather |          2 | Sets the maximum number of parallel processes per executor node.
                    max_worker_processes |          8 | Maximum number of concurrent worker processes.
                        random_page_cost |          4 | Sets the planner's estimate of the cost of a nonsequentially fetched disk page.
                          shared_buffers |      16384 | Sets the number of shared memory buffers used by the server.
                            temp_buffers |       1024 | Sets the maximum number of temporary buffers used by each session.
                                work_mem |       4096 | Sets the maximum memory to be used for query workspaces.
ParameterCheck end Sun Jun 16 11:02:13 CEST 2024
End: Sun Jun 16 11:02:13 CEST 2024
Duration s: 0
```

```
$ ./jbench pg jdbc:postgresql://localhost:5432/postgres arons nopw  prepare

jbench  1.0
Host jvm processors: 8 
Total mem for JVM (MB): 12678 
Start: Sun Jun 16 11:03:09 CEST 2024
DataCreate start Sun Jun 16 11:03:09 CEST 2024
Create table jbench.tbbm_lob
Create table jbench.tbbm_parent
Create table jbench.tbbm_child
Create indexes
Create temp table
Creating data...
Creating data done ms: 24385
Perform statistics
Created 10000 rows into jbench.tbbm_parent
Created 1000000 rows into jbench.tbbm_child
You can now start benchmark
DataCreate end Sun Jun 16 11:03:34 CEST 2024
End: Sun Jun 16 11:03:34 CEST 2024
Duration s: 24
```

```
$ ./jbench pg jdbc:postgresql://localhost:5432/postgres arons nopw  test

jbench  1.0
Host jvm processors: 8 
Total mem for JVM (MB): 12678 
Start: Sun Jun 16 11:14:37 CEST 2024
PostgreSQL 17beta1 arons build on x86_64-pc-linux-gnu, compiled by gcc (Ubuntu 11.4.0-1ubuntu1~22.04) 11.4.0, 64-bit
Connection open latency:
 avg: 4 ms 
 max: 5 ms 
 min: 3 ms 
Perform statistics, ms: 187
Trivial SELECT test with fetch: 18744.164063 qps
=> min. query latency: 0.053350 ms (should be dominated only by network latency. Queries: 57001)
Trivial SELECT test NO fetch: 18391.132813 qps
=> min. query latency: 0.054374 ms (should be dominated only by network latency. Queries: 56001)
Test bynary data bandwidth:
 NULL BLOB insert: 7 ms
 BLOB upload: 49.751244 MB/s
 BLOB in-DB copy: 103.092781 MB/s
 BLOB download: 96.618355 MB/s
Select from larg table:
Time to first row LARGE tab, indexed: 2 ms
Time to last row LARGE tab, indexed: 2 ms
  Rows: 1000
TemporaryTable insert size 1000. Insert: 4 ms. Sort: 2 ms. Commit: 10 ms
TemporaryTable insert size 100000. Insert: 144 ms. Sort: 115 ms. Commit: 9 ms
TemporaryTable insert size 1000000. Insert: 1664 ms. Sort: 1443 ms. Commit: 22 ms
Table Arraysize 1000. Sort: 2 ms. Commit: 1 ms
Table Arraysize 100000. Sort: 190 ms. Commit: 4 ms
Table Arraysize 1000000. Sort: 2114 ms. Commit: 32 ms
Start Parallel client tests...
 Client size:1, max client operations: 5000, commit operation:10,  max running seconds: 30.  running... end. ms:10035
 Client size:4, max client operations: 5000, commit operation:10,  max running seconds: 30.  running... end. ms:11797
 Client size:8, max client operations: 5000, commit operation:10,  max running seconds: 30.  running... end. ms:12905
Parallel client testing results for Treadh:1
  Executions: 5001 
  Executions avg client: 5001.000000 
  Statements: 15003, per second: 1495.067265, per second and client: 1495.067265
  Select avg: 0.305339 ms 
  Insert avg: 0.354529 ms 
  Commit avg: 1.021196 ms 
  Update avg: 0.278344 ms 
  Runtime max: 10035 ms 
Parallel client testing results for Treadh:4
  Executions: 20004 
  Executions avg client: 5001.000000 
  Statements: 60012, per second: 5087.487284, per second and client: 1271.871821
  Select avg: 0.268946 ms 
  Insert avg: 0.335583 ms 
  Commit avg: 1.407319 ms 
  Update avg: 0.266647 ms 
  Runtime max: 11796 ms 
Parallel client testing results for Treadh:8
  Executions: 40008 
  Executions avg client: 5001.000000 
  Statements: 120024, per second: 9301.301922, per second and client: 1162.662740
  Select avg: 0.377225 ms 
  Insert avg: 0.465882 ms 
  Commit avg: 1.304839 ms 
  Update avg: 0.372575 ms 
  Runtime max: 12904 ms 
End: Sun Jun 16 11:15:24 CEST 2024
Duration s: 47
```

```
$ ./jbench pg jdbc:postgresql://localhost:5432/postgres arons nopw  clean
jbench  1.0
Host jvm processors: 8 
Total mem for JVM (MB): 12678 
Start: Sun Jun 16 11:15:51 CEST 2024
DataClean start Sun Jun 16 11:15:51 CEST 2024
Cleaning data...
Cleaning data done
DataClean end Sun Jun 16 11:15:51 CEST 2024
End: Sun Jun 16 11:15:51 CEST 2024
Duration s: 0
```
