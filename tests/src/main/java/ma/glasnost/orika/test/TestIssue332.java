package ma.glasnost.orika.test;

import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestIssue332 {

    @Test
    public void testIssue332() {
        Map<String, String> fieldMap = new HashMap<>();
        fieldMap.put("request.id", "reqId");
        fieldMap.put("request.params['cp']{}", "cp");


        ObjectA a = new ObjectA();
        Request request = new Request();
        request.setId("123");
        a.setRequest(request);


        Map<String, List<String>> params = new HashMap<String, List<String>>();
        params.put("id", new ArrayList<String>() {{
            add("2");
        }});
        params.put("cp", new ArrayList<String>() {{
            add("0.51");
            add("0.52");
            add("0.53");
        }});


        MappingContext.Factory mcf = new MappingContext.Factory();
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().mapNulls(false).mappingContextFactory(mcf).dumpStateOnException(false).build();
        ClassMapBuilder<ObjectA, ObjectB> impBuilder = mapperFactory.classMap(ObjectA.class, ObjectB.class);
        fieldMap.forEach((k, v) -> impBuilder.field(k, v));
        impBuilder.register();

        BoundMapperFacade<ObjectA, ObjectB> delegate = mapperFactory.getMapperFacade(ObjectA.class, ObjectB.class);

        ObjectB b = delegate.map(a);
    }

    public static class ObjectA {

        private Request request;

        public Request getRequest() {
            return request;
        }

        public void setRequest(Request request) {
            this.request = request;
        }
    }

    public static class Request {

        private String id;

        private Map<String, List<String>> params;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Map<String, List<String>> getParams() {
            return params;
        }

        public void setParams(Map<String, List<String>> params) {
            this.params = params;
        }
    }

    public static class ObjectB {

        private Long reqId;

        private Double cp;

        public Long getReqId() {
            return reqId;
        }

        public void setReqId(Long reqId) {
            this.reqId = reqId;
        }

        public Double getCp() {
            return cp;
        }

        public void setCp(Double cp) {
            this.cp = cp;
        }
    }
}
