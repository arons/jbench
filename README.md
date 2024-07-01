# jbench
Jbench is a simple benchmark tool written in java.

### download latest version

as binaries (created with GraalVM for arch x86_64):

```
curl -L  -o jbench  'https://github.com/arons/jbench/releases/download/v_1.1/jbench' && chmod 775 jbench
```

as jar:

```
curl -L  -o jbench.jar  'https://github.com/arons/jbench/releases/download/v_1.1/jbench.jar'
```
Note: in case you want o use the jar, for postgres test, you need to add the postgresql jdbc jar in your classpath.


Below test are done on Digital Ocean droplet g-16vcpu-64gb (see https://slugs.do-api.dev/)

### help
```
$ ./jbench -h
jbench  1.1
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
$ ./jbench info
jbench  1.1
Name: Linux
Version: 6.8.0-36-generic
Arch: amd64

Java version: 21.0.3
Java vendor: Oracle Corporation
Java home: null

Host jvm processors: 16 
Free mem (MB): 32767 max mem (MB): 32768 
Total mem for JVM (MB): 32768 Available processors (cores): 16

File system root: /
Total space (MB): 197315
Free space (MB): 191132
Usable space (MB): 191116
```


### test local disk io
```
$ ./jbench io -h
jbench  1.1
jbench io
options:
 block_size=8 (kb)
 numOfBlocks=256
```

```
$ ./jbench io
jbench  1.1
Mon Jul 01 20:19:10 UTC 2024
Host jvm processors: 16 
Total mem for JVM (MB): 32768 
Start IO test
 block_size=8 (kb)
 numOfBlocks=262144
 fileSize=2048 mb
Location: /home/postgres/.
Total size : 197315 mb
Usable : 191116 mb
Free : 191132 mb
+++ writring test
write IO is 866.3858535258869 MB/s
2048MB written in 2.36 sec
+++  reading test
read IO is 4157.942186985133 MB/s
2048MB written in 0.49 sec
```


### postgres tests

```
$ ./jbench pg -h
jbench  1.1
jbench pg url user password (help|prepare|test|clean|params)

```

```
$ ./jbench pg jdbc:postgresql://localhost:5432/postgres postgres postgres params
jbench  1.1
Host jvm processors: 16 
Total mem for JVM (MB): 32768 
Start: Mon Jul 01 20:17:37 UTC 2024
ParameterCheck start Mon Jul 01 20:17:37 UTC 2024
Version: PostgreSQL 16.1 reda build on x86_64-pc-linux-gnu, compiled by gcc (Ubuntu 13.2.0-23ubuntu4) 13.2.0, 64-bit
Parameters:
            checkpoint_completion_target |        0.9 | Time spent flushing dirty buffers during checkpoint, as fraction of checkpoint interval.
                      checkpoint_timeout |       1200 | Sets the maximum time between automatic WAL checkpoints.
               default_toast_compression |        lz4 | Sets the default compression method for compressible values.
                    effective_cache_size |    6291456 | Sets the planner's assumption about the total size of the data caches.
                    maintenance_work_mem |      65536 | Sets the maximum memory to be used for maintenance operations.
                         max_connections |        300 | Sets the maximum number of concurrent connections.
               max_locks_per_transaction |       5000 | Sets the maximum number of locks per transaction.
                    max_parallel_workers |          0 | Sets the maximum number of parallel workers that can be active at one time.
         max_parallel_workers_per_gather |          0 | Sets the maximum number of parallel processes per executor node.
                    max_worker_processes |         16 | Maximum number of concurrent worker processes.
                        random_page_cost |        1.1 | Sets the planner's estimate of the cost of a nonsequentially fetched disk page.
                          shared_buffers |    2097152 | Sets the number of shared memory buffers used by the server.
                            temp_buffers |       4096 | Sets the maximum number of temporary buffers used by each session.
                                work_mem |      16384 | Sets the maximum memory to be used for query workspaces.
ParameterCheck end Mon Jul 01 20:17:37 UTC 2024
End: Mon Jul 01 20:17:37 UTC 2024
Duration s: 0

```

```
$ ./jbench pg jdbc:postgresql://localhost:5432/postgres postgres postgres prepare
jbench  1.1
Host jvm processors: 16 
Total mem for JVM (MB): 32768 
Start: Mon Jul 01 20:18:12 UTC 2024
DataCreate start Mon Jul 01 20:18:12 UTC 2024
Create table jbench.tbbm_lob
Create table jbench.tbbm_parent
Create table jbench.tbbm_child
Creating data...
Creating data done ms: 9774
Create indexes
Perform statistics
Created 10000 rows into jbench.tbbm_parent
Created 1000000 rows into jbench.tbbm_child
You can now start benchmark
DataCreate end Mon Jul 01 20:18:23 UTC 2024
End: Mon Jul 01 20:18:23 UTC 2024
Duration s: 10
```

```
$ ./jbench pg jdbc:postgresql://localhost:5432/postgres postgres postgres test
jbench  1.1
Host jvm processors: 16 
Total mem for JVM (MB): 32768 
Start: Mon Jul 01 20:12:06 UTC 2024
PostgreSQL 16.1 reda build on x86_64-pc-linux-gnu, compiled by gcc (Ubuntu 13.2.0-23ubuntu4) 13.2.0, 64-bit
Connection open latency:
 avg: 7 ms 
 max: 8 ms 
 min: 7 ms 
Perform statistics, ms: 115
Trivial SELECT test with fetch: 31374.173828 qps
=> min. query latency: 0.031873 ms (should be dominated only by network latency. Queries: 95001)
Trivial SELECT test NO fetch: 30959.054688 qps
=> min. query latency: 0.032301 ms (should be dominated only by network latency. Queries: 93001)
Test bynary data bandwidth:
 NULL BLOB insert: 2 ms
 BLOB upload: 63.897766 MB/s
 BLOB in-DB copy: 125.000000 MB/s
 BLOB download: 83.682014 MB/s
Select from larg table:
Time to first row LARGE tab, indexed: 23 ms
Time to last row LARGE tab, indexed: 25 ms
  Rows: 36700
TemporaryTable insert size 1000. Insert: 2 ms. Sort: 2 ms. Commit: 1 ms
TemporaryTable insert size 100000. Insert: 95 ms. Sort: 90 ms. Commit: 3 ms
TemporaryTable insert size 1000000. Insert: 1512 ms. Sort: 1104 ms. Commit: 13 ms
Table Arraysize 1000. Sort: 3 ms. Commit: 0 ms
Table Arraysize 100000. Sort: 143 ms. Commit: 2 ms
Table Arraysize 1000000. Sort: 1754 ms. Commit: 12 ms
First filling buffer cache...
 Client size:64, max client operations: 5000, commit operation:10,  max running seconds: 10.  running ... end. ms:8969  statements:960190  per seconds:120023
Parallel client testing results for Treadh:64
  Executions: 320064 , avg client: 5001.000000 
  Statements: 960190 (1), avg: 15002, min: 15001 max: 15003 
  per second: 106877.782725, per second and client: 1669.965355
  Select avg: 0.475011 ms 
  Insert avg: 0.623266 ms 
  Commit avg: 0.194964 ms 
  Update avg: 0.424524 ms 
  Runtime max: 8984 ms 
Start Parallel client tests...
 Client size:1, max client operations: 5000, commit operation:10,  max running seconds: 10.  running ... end. ms:964  statements:15001  
 Client size:2, max client operations: 5000, commit operation:10,  max running seconds: 10.  running ... end. ms:1021  statements:30006  per seconds:30006
 Client size:4, max client operations: 5000, commit operation:10,  max running seconds: 10.  running ... end. ms:1249  statements:60012  per seconds:60012
 Client size:6, max client operations: 5000, commit operation:10,  max running seconds: 10.  running ... end. ms:1560  statements:90018  per seconds:90018
 Client size:8, max client operations: 5000, commit operation:10,  max running seconds: 10.  running ... end. ms:1756  statements:120024  per seconds:120024
 Client size:10, max client operations: 5000, commit operation:10,  max running seconds: 10.  running ... end. ms:1960  statements:150030  per seconds:150030
 Client size:12, max client operations: 5000, commit operation:10,  max running seconds: 10.  running ... end. ms:2132  statements:180036  per seconds:90018
 Client size:14, max client operations: 5000, commit operation:10,  max running seconds: 10.  running ... end. ms:2390  statements:210042  per seconds:105021
 Client size:16, max client operations: 5000, commit operation:10,  max running seconds: 10.  running ... end. ms:2583  statements:240048  per seconds:120024
 Client size:32, max client operations: 5000, commit operation:10,  max running seconds: 10.  running ... end. ms:4481  statements:480096  per seconds:120024
 Client size:48, max client operations: 5000, commit operation:10,  max running seconds: 10.  running ... end. ms:6724  statements:720144  per seconds:120024
 Client size:64, max client operations: 5000, commit operation:10,  max running seconds: 10.  running ... end. ms:8968  statements:960192  per seconds:120024
Parallel client testing results for Treadh:1
  Executions: 5001 , avg client: 5001.000000 
  Statements: 15001 (1), avg: 15001, min: 15001 max: 15001 
  per second: 15561.203320, per second and client: 15561.203320
  Select avg: 0.052589 ms 
  Insert avg: 0.067586 ms 
  Commit avg: 0.026395 ms 
  Update avg: 0.041992 ms 
  Runtime max: 964 ms 
Parallel client testing results for Treadh:2
  Executions: 10002 , avg client: 5001.000000 
  Statements: 30006 (0), avg: 15003, min: 15003 max: 15003 
  per second: 29388.834476, per second and client: 14694.417238
  Select avg: 0.052190 ms 
  Insert avg: 0.066187 ms 
  Commit avg: 0.028794 ms 
  Update avg: 0.051590 ms 
  Runtime max: 1021 ms 
Parallel client testing results for Treadh:4
  Executions: 20004 , avg client: 5001.000000 
  Statements: 60012 (0), avg: 15003, min: 15003 max: 15003 
  per second: 48048.038431, per second and client: 12012.009608
  Select avg: 0.059238 ms 
  Insert avg: 0.080784 ms 
  Commit avg: 0.025895 ms 
  Update avg: 0.060088 ms 
  Runtime max: 1249 ms 
Parallel client testing results for Treadh:6
  Executions: 30006 , avg client: 5001.000000 
  Statements: 90018 (0), avg: 15003, min: 15003 max: 15003 
  per second: 57703.846154, per second and client: 9617.307692
  Select avg: 0.069020 ms 
  Insert avg: 0.100413 ms 
  Commit avg: 0.024862 ms 
  Update avg: 0.071119 ms 
  Runtime max: 1560 ms 
Parallel client testing results for Treadh:8
  Executions: 40008 , avg client: 5001.000000 
  Statements: 120024 (0), avg: 15003, min: 15003 max: 15003 
  per second: 68350.797267, per second and client: 8543.849658
  Select avg: 0.085083 ms 
  Insert avg: 0.118451 ms 
  Commit avg: 0.031144 ms 
  Update avg: 0.087383 ms 
  Runtime max: 1756 ms 
Parallel client testing results for Treadh:10
  Executions: 50010 , avg client: 5001.000000 
  Statements: 150030 (0), avg: 15003, min: 15003 max: 15003 
  per second: 76545.918367, per second and client: 7654.591837
  Select avg: 0.093741 ms 
  Insert avg: 0.132933 ms 
  Commit avg: 0.035453 ms 
  Update avg: 0.098560 ms 
  Runtime max: 1960 ms 
Parallel client testing results for Treadh:12
  Executions: 60012 , avg client: 5001.000000 
  Statements: 180036 (0), avg: 15003, min: 15003 max: 15003 
  per second: 84444.652908, per second and client: 7037.054409
  Select avg: 0.105362 ms 
  Insert avg: 0.143871 ms 
  Commit avg: 0.036259 ms 
  Update avg: 0.109661 ms 
  Runtime max: 2132 ms 
Parallel client testing results for Treadh:14
  Executions: 70014 , avg client: 5001.000000 
  Statements: 210042 (0), avg: 15003, min: 15003 max: 15003 
  per second: 87883.682008, per second and client: 6277.405858
  Select avg: 0.116105 ms 
  Insert avg: 0.157997 ms 
  Commit avg: 0.050390 ms 
  Update avg: 0.117948 ms 
  Runtime max: 2390 ms 
Parallel client testing results for Treadh:16
  Executions: 80016 , avg client: 5001.000000 
  Statements: 240048 (0), avg: 15003, min: 15003 max: 15003 
  per second: 92897.832817, per second and client: 5806.114551
  Select avg: 0.129474 ms 
  Insert avg: 0.169654 ms 
  Commit avg: 0.049778 ms 
  Update avg: 0.129474 ms 
  Runtime max: 2584 ms 
Parallel client testing results for Treadh:32
  Executions: 160032 , avg client: 5001.000000 
  Statements: 480096 (0), avg: 15003, min: 15003 max: 15003 
  per second: 107116.465863, per second and client: 3347.389558
  Select avg: 0.226736 ms 
  Insert avg: 0.290992 ms 
  Commit avg: 0.092182 ms 
  Update avg: 0.219069 ms 
  Runtime max: 4482 ms 
Parallel client testing results for Treadh:48
  Executions: 240048 , avg client: 5001.000000 
  Statements: 720144 (0), avg: 15003, min: 15003 max: 15003 
  per second: 107068.688671, per second and client: 2230.597681
  Select avg: 0.360132 ms 
  Insert avg: 0.451151 ms 
  Commit avg: 0.141513 ms 
  Update avg: 0.328843 ms 
  Runtime max: 6726 ms 
Parallel client testing results for Treadh:64
  Executions: 320064 , avg client: 5001.000000 
  Statements: 960192 (0), avg: 15003, min: 15003 max: 15003 
  per second: 107044.816054, per second and client: 1672.575251
  Select avg: 0.485728 ms 
  Insert avg: 0.613205 ms 
  Commit avg: 0.197789 ms 
  Update avg: 0.426911 ms 
  Runtime max: 8970 ms 
End: Mon Jul 01 20:13:02 UTC 2024
Duration s: 56

```

```
$ ./jbench pg jdbc:postgresql://localhost:5432/postgres postgres postgres clean
jbench  1.1
Host jvm processors: 16 
Total mem for JVM (MB): 32768 
Start: Mon Jul 01 20:18:08 UTC 2024
DataClean start Mon Jul 01 20:18:08 UTC 2024
Cleaning data...
Cleaning data done
DataClean end Mon Jul 01 20:18:09 UTC 2024
End: Mon Jul 01 20:18:09 UTC 2024
Duration s: 0
```
