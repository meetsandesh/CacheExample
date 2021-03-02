# CacheExample
 Problem Statement 1:

- We have to design a Cache as a framework piece which can be used throughout the application to cache required data.
- Each cache instance can hold only 'one' type of cached objects.. at the time of instantiation we should be able to define the target object's class e.g. Car, Person, Application, String etc.
- Each cached object has a key by which it can be fetched. The cache will have only 1 public method i.e. get(key) which will return the object associated with that key.
- The cache capacity should be configurable at the time of instantiation of cache i.e. I can define that the cache will only hold 100 objects
- The cache should be configurable to have 'n' seconds expiry time for each value.. this should be also fixed at the time of instantiation
- If a value of a given key expires, the cache should be able to replenish the value. This should happen at the get(key) call. No background activity.
- To replenish the value, there must be a mechanism that is passed as callback to the cache at the time of instantiation and the callback should return value corresponding to the key.
- No initial loading, totally lazy building i.e. cache will be initially empty
- internal LRU tracking.. the least recently used 10% entries should be automatically removed when the cache gets full to the specified capacity
- No external library to be used. Pure java code. Think of functional solution first. Later refine for performance.

 Problem Statement 2:

- Cache should only store Serializable (java.io.Serializable) objects.
- Have additional configuration property of “memory threshold size” in cache configuration beyond which the cache should start storing serialized values on the disk (keeping MRU and LRU access order in mind).
- The serialization and storage of cache entry to the disk should be “asynchronous”. Reading back from the disk should be synchronous. Deletion of disk entries should be “low priority asynchronous”.
- There should be provision of “listening” the cache entry eviction. By default eviction should be logged on System.out.println if no listener is attached to the cache.
- Cache should have a method called “getStatistics()” which returns a Pojo with statistics of cache performance. The following should be published in statistics
	- Current total cache size (number of entries)
	- Current memory size (number of entries)
	- Current disk size (number of entries)
	- Total access count (number of times cache is accessed)
	- Hit ratio (% of cache successful hit)
	- Miss ratio (% of cache miss that resulted in replenishing of the value)
	- Avg LRU optimization time spent in milliseconds
	- Avg value replenishment time spent in milliseconds

