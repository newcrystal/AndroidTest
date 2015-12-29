package com.crystal.httpdebugger.db;

import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.crystal.httpdebugger.proxy.domain.HttpRequest;
import com.crystal.httpdebugger.proxy.domain.HttpResponse;

public class DatabaseQuery {
	private static final String TABLE_REQUEST_BODY = "RequestBody";
	private static final String TABLE_RESPONSE_BODY = "ResponseBody";
	private static final String TABLE_REQUEST_HEADER = "RequestHeader";
	private static final String TABLE_RESPONSE_BASE_INFO = "ResponseBaseInfo";
	private static final String TABLE_RESPONSE_HEADER = "ResponseHeader";
	private static final String TABLE_REQUEST_BASE_INFO = "RequestBaseInfo";
	private SQLiteDatabase db;

	public DatabaseQuery(Context context) {
		db = context.openOrCreateDatabase("HTTPDebugger", Context.MODE_PRIVATE, null);
		createTable();
	}

	private void createTable() {
		if (!isCreatedTable(TABLE_REQUEST_BASE_INFO)) {
			db.execSQL("create table RequestBaseInfo (id integer PRIMARY KEY autoincrement, method varchar not null, url varchar not null, protocol varchar not null, protocolVersion varchar not null);");
		}
		if (!isCreatedTable(TABLE_REQUEST_HEADER)) {
			db.execSQL("create table RequestHeader (id integer, name varchar not null, value varchar not null);");
		}
		if (!isCreatedTable(TABLE_REQUEST_BODY)) {
			db.execSQL("create table RequestBody (id integer, body text);");
		}
		if (!isCreatedTable(TABLE_RESPONSE_BASE_INFO)) {
			db.execSQL("create table ResponseBaseInfo (id integer, statusCode varchar, message varchar, protocol varchar, responseTime integer);");
		}
		if (!isCreatedTable(TABLE_RESPONSE_HEADER)) {
			db.execSQL("create table ResponseHeader (id integer, name varchar not null, value varchar not null);");
		}
		if (!isCreatedTable(TABLE_RESPONSE_BODY)) {
			db.execSQL("create table ResponseBody (id integer, body text);");
		}
	}

	private boolean isCreatedTable(String tableName) {
		Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.close();
				return true;
			}
			cursor.close();
		}
		return false;
	}
	
	public int insertRequest(HttpRequest request) {
		int id = 0;
		if (!request.isEmpty()) {
			id = insertRequestBaseInfo(request);
			request.setId(id);
			if (request.getHeader().size() != 0) insertRequestHeader(request);
			if (!request.getBody().isEmpty()) insertRequestBody(request);
		}
		return id;
	}
	
	public void insertResponse(HttpResponse response) {
		if (!response.isEmpty()) {
			insertResponseBaseInfo(response);
			if (response.getHeader().size() != 0) insertResponseHeader(response);
			if (!(response.getBody() == null || response.getBody().isEmpty())) insertResponseBody(response);
		}
	}

	private int insertRequestBaseInfo(HttpRequest request) {
		ContentValues recordValues = new ContentValues();
		recordValues.put("method", request.getMethod());
		recordValues.put("url", request.getUrl());
		recordValues.put("protocol", request.getProtocol());
		recordValues.put("protocolVersion", request.getProtocolVersion());
	
		return (int) db.insert(TABLE_REQUEST_BASE_INFO, null, recordValues);
	}

	private void insertRequestHeader(HttpRequest request) {
		for (Map.Entry<String, String> entry : request.getHeader().entrySet()) {
			ContentValues recordValues = new ContentValues();
			recordValues.put("id", request.getId());
			recordValues.put("name", entry.getKey());
			recordValues.put("value", entry.getValue());
			db.insert(TABLE_REQUEST_HEADER, null, recordValues);
		}
	}

	private void insertRequestBody(HttpRequest request) {
		ContentValues recordValues = new ContentValues();
		recordValues.put("id", request.getId());
		recordValues.put("body", request.getBody());
		db.insert(TABLE_REQUEST_BODY, null, recordValues);
	}

	private void insertResponseBaseInfo(HttpResponse response) {
		ContentValues recordValues = new ContentValues();
		recordValues.put("id", response.getId());
		recordValues.put("statusCode", response.getStatusCode());
		recordValues.put("message", response.getMessage());
		recordValues.put("protocol", response.getProtocol());
		recordValues.put("responseTime", response.getResponseTime());
		db.insert(TABLE_RESPONSE_BASE_INFO, null, recordValues);
	}

	private void insertResponseHeader(HttpResponse response) {
		for (Map.Entry<String, String> entry : response.getHeader().entrySet()) {
			ContentValues recordValues = new ContentValues();
			recordValues.put("id", response.getId());
			recordValues.put("name", entry.getKey());
			recordValues.put("value", entry.getValue());
			db.insert(TABLE_RESPONSE_HEADER, null, recordValues);
		}
	}

	private void insertResponseBody(HttpResponse response) {
		ContentValues recordValues = new ContentValues();
		recordValues.put("id", response.getId());
		recordValues.put("body", response.getBody());

		db.insert(TABLE_RESPONSE_BODY, null, recordValues);
	}

	private void setRequestBaseInfo(HttpRequest request) {
		String[] columns = { "method", "url", "protocol", "protocolVersion" };
		String whereStr = "id = ?";
		String[] whereParams = { String.valueOf(request.getId()) };

		Cursor cursor = db.query(TABLE_REQUEST_BASE_INFO, columns, whereStr, whereParams, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToNext();
			request.setMethod(cursor.getString(0));
			request.setUrl(cursor.getString(1));
			request.setProtocol(cursor.getString(2));
			request.setProtocolVersion(cursor.getString(3));
		}
		cursor.close();
	}
	
	public HttpRequest getRequest(int id) {
		HttpRequest request = new HttpRequest();
		request.setId(id);
		setRequestBaseInfo(request);
		setRequestHeader(request);
		setRequestBody(request);
		return request;
	}
	
	public HttpResponse getResponse(int id) {
		HttpResponse response = new HttpResponse();
		response.setId(id);
		setResponseBaseInfo(response);
		setResponseHeader(response);
		setResponseBody(response);
		return response;
	}
 
	private void setRequestHeader(HttpRequest request) {
		String[] columns = { "name", "value" };
		String whereStr = "id = ?";
		String[] whereParams = { String.valueOf(request.getId()) };
		Cursor cursor = db.query(TABLE_REQUEST_HEADER, columns, whereStr, whereParams, null, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			while(cursor.moveToNext()) {
				request.addHeader(cursor.getString(0), cursor.getString(1));
			}
		}
		cursor.close();
	}

	private void setRequestBody(HttpRequest request) {
		String[] columns = { "body" };
		String whereStr = "id = ?";
		String[] whereParams = { String.valueOf(request.getId()) };
		Cursor cursor = db.query(TABLE_REQUEST_BODY, columns, whereStr, whereParams, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToNext();
			request.setBody(cursor.getString(0));
		}
		cursor.close();
	}

	private void setResponseBaseInfo(HttpResponse response) {
		String[] columns = { "statusCode", "message", "protocol", "responseTime" };
		String whereStr = "id = ?";
		String[] whereParams = { String.valueOf(response.getId()) };
		Cursor cursor = db.query(TABLE_RESPONSE_BASE_INFO, columns, whereStr, whereParams, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToNext();
			response.setStatusCode(cursor.getString(0));
			response.setMessage(cursor.getString(1));
			response.setProtocol(cursor.getString(2));
			response.setResponseTime(cursor.getInt(3));
		}
		cursor.close();
	}

	private void setResponseHeader(HttpResponse response) {
		String[] columns = { "name", "value" };
		String whereStr = "id = ?";
		String[] whereParams = { String.valueOf(response.getId()) };
		Cursor cursor = db.query(TABLE_RESPONSE_HEADER, columns, whereStr, whereParams, null, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			while(cursor.moveToNext()) {
				response.add(cursor.getString(0), cursor.getString(1));
			}
		}
		cursor.close();
	}

	private void setResponseBody(HttpResponse response) {
		String[] columns = { "body" };
		String whereStr = "id = ?";
		String[] whereParams = { String.valueOf(response.getId()) };
		Cursor cursor = db.query(TABLE_RESPONSE_BODY, columns, whereStr, whereParams, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToNext();
			response.setBody(cursor.getString(0));
		}
		cursor.close();
	}
	
	public void deleteAll() {
		int count = db.delete(TABLE_REQUEST_BODY, null, null);
		System.out.println(TABLE_REQUEST_BODY + " "+ count + " deleted.");
		count = db.delete(TABLE_RESPONSE_BODY, null, null);
		System.out.println(TABLE_RESPONSE_BODY + " "+ count + " deleted.");
		count = db.delete(TABLE_REQUEST_BASE_INFO, null, null);
		System.out.println(TABLE_REQUEST_BASE_INFO + " "+ count + " deleted.");
		count = db.delete(TABLE_RESPONSE_BASE_INFO, null, null);
		System.out.println(TABLE_RESPONSE_BASE_INFO + " "+ count + " deleted.");
		count = db.delete(TABLE_REQUEST_HEADER, null, null);
		System.out.println(TABLE_REQUEST_HEADER + " "+ count + " deleted.");
		count = db.delete(TABLE_RESPONSE_HEADER, null, null);
		System.out.println(TABLE_RESPONSE_HEADER + " "+ count + " deleted.");
	}
}
