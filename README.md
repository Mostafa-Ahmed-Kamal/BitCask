# BitCask
Bitcask is a log-structured key-value storage system designed for high-performance, high-throughput applications. It provides a simple yet efficient way to store and retrieve key-value data with low latency and high scalability.

# Functionalities:
- public put(String key, String value) -> void: store a key-value pair ✅
- public get(String key) -> String value: Retrieves the value associated with a given key. ✅
- public delete(String key) -> void: Remove a key-value pair ❌
- private compaction() -> void: a scheduled operation that merges and compacts log files to create new log files with smaller size and generates hint files to help with rebuilding after system restart/failure. ✅

### Note: 
delete operation and checksum are not yet implemented. 
# Resources:
https://docs.riak.com/riak/kv/2.2.3/setup/planning/backend/bitcask/index.html 

https://riak.com/assets/bitcask-intro.pdf
