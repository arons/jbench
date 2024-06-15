# jbench


### download latest version
```
curl -L  -o jbench  'https://github.com/arons/jbench/releases/download/v_1.0b/jbench'  && chmod 775 jbench
```

### help
```
$ ./jbench -h
jbench 1.0b
Usage: jbench (info,io) 
commands:
 info :  print some system info
 io   :  performs file system read/write test
 pg   :  performs postgres tests
```

### get system info
```
./jbench info
```


### test local disk io
```
$ ./jbench io -h
jbench 1.0b
jbench io
options:
 block_size=8 (kb)
 numOfBlocks=256
```


### postgres tests


```
$ ./jbench pg -h
jbench 1.0b
jbench pg url user password (help|prepare|test|clean|params)

```
