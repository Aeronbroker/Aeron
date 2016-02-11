package eu.neclab.iotplatform.iotbroker.commons;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.SetMultimap;

public class HashMultiBiMap<K, V> implements SetMultimap<K, V> {

	private Multimap<K, V> direct = HashMultimap.create();
	private Multimap<V, K> reverse = HashMultimap.create();

	@Override
	public void clear() {
		direct.clear();
		reverse.clear();
	}

	@Override
	public boolean containsEntry(Object key, Object value) {
		return direct.containsEntry(key, value);
	}

	@Override
	public boolean containsKey(Object key) {
		return direct.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return direct.containsValue(value);
	}

	@Override
	public boolean isEmpty() {
		return direct.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return direct.keySet();
	}

	public Set<V> reverseKeySet() {
		return reverse.keySet();
	}

	@Override
	public Multiset<K> keys() {
		return direct.keys();
	}

	public Multiset<V> reverseKeys() {
		return reverse.keys();
	}

	@Override
	public boolean put(K key, V value) {
		return direct.put(key, value) && reverse.put(value, key);
	}

	@Override
	public boolean putAll(Multimap<? extends K, ? extends V> multimap) {
		boolean changed = false;
		for (Entry<? extends K, ? extends V> entry : multimap.entries()) {
			changed = put(entry.getKey(), entry.getValue()) || changed;
		}
		return changed;
	}

	@Override
	public boolean putAll(K key, Iterable<? extends V> iterable) {
		boolean changed = false;
		for (V value : iterable) {
			changed = put(key, value) || changed;
		}
		return changed;
	}

	public boolean reversePutAll(V value, Iterable<? extends K> iterable) {
		boolean changed = false;
		for (K key : iterable) {
			changed = put(key, value) || changed;
		}
		return changed;
	}

	@Override
	public boolean remove(Object key, Object value) {
		return direct.remove(key, value) && reverse.remove(value, key);
	}

	public boolean reverseRemove(Object value, Object key) {
		return remove(key, value);
	}

	@Override
	public int size() {
		return direct.size();
	}

	@Override
	public Collection<V> values() {
		return direct.values();
	}

	public Collection<K> reverseValues() {
		return reverse.values();
	}

	@Override
	public Map<K, Collection<V>> asMap() {
		return direct.asMap();
	}

	public Map<V, Collection<K>> reverseAsMap() {
		return reverse.asMap();
	}

	@Override
	public Set<Entry<K, V>> entries() {
		return (Set<Map.Entry<K, V>>) direct.entries();
	}

	public Set<Entry<V, K>> reverseEntries() {
		return (Set<Map.Entry<V, K>>) reverse.entries();
	}

	@Override
	public Set<V> get(K key) {
		return (Set<V>) direct.get(key);
	}

	public Set<K> reverseGet(V value) {
		return (Set<K>) reverse.get(value);
	}

	@Override
	public Set<V> removeAll(Object key) {
		Set<V> removedValues = (Set<V>) direct.removeAll(key);
		for (V value : removedValues) {
			reverse.remove(value, key);
		}
		return removedValues;
	}

	public Set<K> reverseRemoveAll(Object value) {
		Set<K> removedKeys = (Set<K>) reverse.removeAll(value);
		for (K key : removedKeys) {
			direct.remove(key, value);
		}
		return removedKeys;
	}

	@Override
	public Set<V> replaceValues(K key, Iterable<? extends V> values) {
		Set<V> replacedValues = (Set<V>) direct.replaceValues(key,values);
		for (V value : replacedValues) {
			reverse.remove(value, key);
		}
		for (V value : values) {
			reverse.put(value, key);
		}
		return replacedValues;
	}
	
	public Set<K> reverseReplaceValues(V value, Iterable<? extends K> keys) {
		Set<K> replacedKeys = (Set<K>) reverse.replaceValues(value,keys);
		for (K key : replacedKeys) {
			direct.remove(key, value);
		}
		for (K key : keys) {
			direct.remove(key, value);
		}
		return replacedKeys;
	}

}
