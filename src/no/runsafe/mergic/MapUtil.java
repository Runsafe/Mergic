package no.runsafe.mergic;

import java.util.*;

public class MapUtil
{
	public static <K, V extends Comparable<? super V>>Map<K, V> sortByValue(Map<K, V> map)
	{
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>()
		{
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2)
			{
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list)
			result.put(entry.getKey(), entry.getValue());

		return result;
	}

	public static <K, V>Map<K, V> limitMap(Map<K, V> map, int amount)
	{
		HashMap<K, V> newMap = new HashMap<K, V>();
		int curr = 0;

		for (Map.Entry<K, V> node : map.entrySet())
		{
			if (curr == amount)
				return newMap;

			newMap.put(node.getKey(), node.getValue());
			curr++;
		}
		return newMap;
	}
}
