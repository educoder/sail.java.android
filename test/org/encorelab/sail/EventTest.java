package org.encorelab.sail;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class EventTest {

	@Test
	public void testToJsonWithSimplePayloads() {
		Event ev;
		String json;
		
		ev = new Event("test", "Testing");
		json = "{\"eventType\":\"test\",\"payload\":\"Testing\"}";
		assertEquals(json, ev.toJson());
		
		ev = new Event("test", 123);
		json = "{\"eventType\":\"test\",\"payload\":123}";
		assertEquals(json, ev.toJson());
	}
	
	@Test
	public void testToJsonWithComplexPayloads() {
		Event ev;
		String json;
		Map<String,Object> h;
		Map<String,Object> h2;
		int[] arr1 = {1,2,3,5,7};
		String[] arr2 = {"a","b","c"};
		
		// using a LinkedHashMap rather than plain HashMap to preserve key order for easier testing
		h = new LinkedHashMap<String,Object>();
		h.put("testString", "Testing");
		h.put("testInt", 123);
		h.put("nothing", null);
		ev = new Event("test", h);
		json = "{\"eventType\":\"test\",\"payload\":{\"testString\":\"Testing\",\"testInt\":123}}";
		assertEquals(json, ev.toJson());
		
		h = new LinkedHashMap<String,Object>();
		h2 = new LinkedHashMap<String,Object>();
		h2.put("testString", "Testing");
		h2.put("arr2", arr2);
		h.put("h2", h2);
		h.put("arr1", arr1);
		ev = new Event("test", h);
		json = "{\"eventType\":\"test\",\"payload\":{\"h2\":{\"testString\":\"Testing\",\"arr2\":[\"a\",\"b\",\"c\"]},\"arr1\":[1,2,3,5,7]}}";
		assertEquals(json, ev.toJson());
	}

	@Test
	public void testFromJsonWithSimplePayloads() {
		Event ev;
		String json;
		
		json = "{\"eventType\":\"test\",\"payload\":123}";
		ev = Event.fromJson(json);
		
		assertEquals("test", ev.getType());
		assertEquals(123, ev.getPayload());
		assertEquals(133, ev.getPayloadAsInt() + 10);
		
		json = "{\"eventType\":\"test\",\"payload\":\"Testing\"}";
		ev = Event.fromJson(json);
		
		assertEquals("test", ev.getType());
		assertEquals("Testing", ev.getPayload());
		assertEquals("Testing123", ev.getPayloadAsString() + "123");
	}
	
	@Test
	public void testFromJsonWithComplexPayloads() {
		Event ev;
		String json;
		
		json = "{\"eventType\":\"test\",\"payload\":{\"testString\":\"Testing\",\"testInt\":123}}";
		ev = Event.fromJson(json);
		
		assertEquals("test", ev.getType());
		assertEquals("Testing", ev.getPayloadAsMap().get("testString"));
		assertEquals(123, ev.getPayloadAsMap().get("testInt"));
		
		json = "{\"eventType\":\"test\",\"payload\":{\"h2\":{\"testString\":\"Testing\",\"arr2\":[\"a\",\"b\",\"c\"]},\"arr1\":[1,2,3,5,7]}}";
		ev = Event.fromJson(json);
		
		assertEquals("test", ev.getType());
		assertEquals("Testing", ((Map<String,Object>) ev.getPayloadAsMap().get("h2")).get("testString"));
		assertEquals(5, ((List<Number>) ev.getPayloadAsMap().get("arr1")).get(3));
		assertEquals("b", ((List<String>) ((Map<String,Object>) ev.getPayloadAsMap().get("h2")).get("arr2")).get(1));
	}

}
