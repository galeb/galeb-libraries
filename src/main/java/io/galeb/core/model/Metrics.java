package io.galeb.core.model;

import io.galeb.core.json.JsonObject;

public class Metrics extends Entity {

    private static final long serialVersionUID = 4453537887347058918L;

    public static final String METRICS_QUEUE = "METRICS_QUEUE";

    public static final String PROP_METRICS_TOTAL = "METRICS_TOTAL";

    public static final String PROP_STATUSCODE = "status";

    public static final String PROP_HTTPCODE_PREFIX = "httpCode";

    public static final String PROP_REQUESTTIME = "requestTime";

    public static final String PROP_REQUESTTIME_AVG = "requestTimeAvg";

    public Metrics() {
        // Default
    }

    public Metrics(Metrics metrics) {
        final String metricsStr = JsonObject.toJsonString(metrics);
        final Metrics newMetrics = (Metrics) JsonObject.fromJson(metricsStr, Metrics.class);

        setId(newMetrics.getId());
        setParentId(newMetrics.getParentId());
        setProperties(newMetrics.getProperties());
        updateHash();
    }

}
