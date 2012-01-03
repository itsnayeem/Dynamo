<h1>Java Implementation of Dynamo</h1>
<h4>Final Project for CS682 - Distributed Software Development</h4>
<br />
<h3>Overview</h3>
<p>This is a partial implementation of Amazon's Dynamo. Although it is
functioning, it is not a complete project. This implementation supports:</p>
<ul>
<li>Distributed Hash Table (DHT)</li>
<li>Virtual Nodes evenly distributed amongst physical servers</li>
<li>Data replication up to <em>n</em> copies with version control</li>
<li>Frontend servers use this system to store and retrieve 'tweets'</li>
<li>Able to recover from network disruption</li>
</ul>
<h3>3rd-party libraries used</h3>
<ul>
<li><a href="http://code.google.com/p/google-gson/">Gson</a></li>
<li><a href="http://logging.apache.org/log4j/1.2/">Log4j</a></li>
<li><a href="http://hc.apache.org/">HttpCore/Client</a></li>
</ul>
