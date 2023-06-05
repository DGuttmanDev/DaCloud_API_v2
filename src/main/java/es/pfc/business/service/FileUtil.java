package es.pfc.business.service;

import es.pfc.business.model.Archivo;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;

import java.io.*;

public class FileUtil {

    public MultipartFile generateMultipartFile(Archivo archivo) throws IOException {
        File file = new File(archivo.getPath());

        FileInputStream fileInputStream = new FileInputStream(file);

        MultiValueMap<String, Object> multipartMap = new LinkedMultiValueMap<>();
        multipartMap.add("file", StreamUtils.copyToByteArray(fileInputStream));

        MultipartFile multipartFile = new CustomMultipartFile(
                file.getName(),
                file.getName(),
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                multipartMap
        );

        return multipartFile;
    }

    private static class CustomMultipartFile implements MultipartFile {
        private final String name;
        private final String originalFilename;
        private final String contentType;
        private final byte[] content;

        public CustomMultipartFile(String name, String originalFilename, String contentType, MultiValueMap<String, Object> multipartMap) throws IOException {
            this.name = name;
            this.originalFilename = originalFilename;
            this.contentType = contentType;
            this.content = (byte[]) multipartMap.getFirst("file");
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getOriginalFilename() {
            return originalFilename;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public boolean isEmpty() {
            return content.length == 0;
        }

        @Override
        public long getSize() {
            return content.length;
        }

        @Override
        public byte[] getBytes() throws IOException {
            return content;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(content);
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            try (OutputStream outputStream = new FileOutputStream(dest)) {
                outputStream.write(content);
            }
        }
    }
}

