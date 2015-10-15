package eu.neclab.iotplatform.couchdb.util;

public enum View {

	// @formatter:off
	
	ATTRIBUTE_BY_ID_VIEW(
			"_design/attributesById",
			"function(doc) {" +
				"if (/"+ CouchDBUtil.LATEST_VALUE_PREFIX +".*/.test(doc._id)) {" +
					"var idAndAttribute = doc._id.split(\\\""+ CouchDBUtil.PREFIX_TO_ID_SEPARATOR +"\\\")[1].split(\\\""+ CouchDBUtil.ID_TO_ATTRIBUTENAME_SEPARATOR+"\\\");" +
					"emit(idAndAttribute[0], idAndAttribute[1]);" +
				"}" + 
			"}"),
	
	ID_BY_TYPE_VIEW(
			"_design/idsByType",
			"function(doc) {" +
			    "if (/"+ CouchDBUtil.LATEST_VALUE_PREFIX +".*/.test(doc._id)) {" +
					"if (doc.entityId){" +
						"if (doc.entityId.type){" +
							"var idAndAttribute = doc._id.split(\\\""+ CouchDBUtil.PREFIX_TO_ID_SEPARATOR +"\\\")[1];" +
							"emit(doc.entityId.type, idAndAttribute);" +
						"}" +
					"}" +
			    "}" +
			"}");
	
	// @formatter:on

	private String path;
	private String view;

	private View(String path, String view) {
		this.path = path;
		this.view = view;
	}

	public String getPath() {
		return path;
	}

	public String getView() {
		return view;
	}

	public String getReadyToStoreView() {
		return "{\"views\":{\"query\":{\"map\":\"" + this.getView() + "\"}}}";
	}

	public String getQueryPath() {
		return this.getPath() + "/_view/query";

	}

}
