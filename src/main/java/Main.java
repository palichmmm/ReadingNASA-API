import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.*;
import java.io.*;
import java.net.URL;
import java.util.List;

public class Main {
    public static final String URL = "https://api.nasa.gov/planetary/apod?api_key=VRhef6lhLycihipgLjnU0qfpRnPT7XLhUaI97gcd";
    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException{
        try (CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setUserAgent("My Test NASA")
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build()) {
            HttpGet request = new HttpGet(URL);
            request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
                List<ApiNasa> apiNasas = mapper.readValue(
                        response.getEntity().getContent(),
                        new TypeReference<>() {
                        });
                boolean result = downloadFileUrl(apiNasas.get(0).getUrl());
                if (result) {
                    System.out.println(" Файл успешно загружен!");
                } else {
                    System.out.println(" Файл не загружен!!!!!");
                }
            }
        }
    }
    public static boolean downloadFileUrl(String url) {
        try{
            String fileName = new File(url).getName();
            System.out.println("Downloading File From: " + url);
            URL fileUrl = new URL(url);
            try (InputStream inputStream = fileUrl.openStream();
                 OutputStream outputStream = new FileOutputStream(fileName)) {
                byte[] buffer = new byte[2048];
                int length = 0;
                while ((length = inputStream.read(buffer)) != -1) {
                    System.out.print("##");
                    Thread.sleep(100); // Анимация загрузки файла
                    outputStream.write(buffer, 0, length);
                }
            }
        } catch(Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return false;
        }
        return true;
    }
}