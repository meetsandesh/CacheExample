# CacheExample
 Problem Statement:

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