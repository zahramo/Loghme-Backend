package ir.ac.ut.ece.ie.loghme.repository.http;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class LoghmeHttpClient {
    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    public String responseData;

    public void sendGet(String uri) throws Exception {
        HttpGet request = new HttpGet(uri);
        try(CloseableHttpResponse response = httpClient.execute(request)){
            HttpEntity entity = response.getEntity();
            Header headers = entity.getContentType();

            if (entity != null) {
                responseData = EntityUtils.toString(entity);
                response.close();
            }

        }
    }

    public void close() throws IOException {
        //httpClient.close();
    }

    public String getResponseData() {
        return responseData;
    }
}
