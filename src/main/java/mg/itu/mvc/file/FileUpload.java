package mg.itu.mvc.file;

import java.util.HashMap;
import java.util.Map;

public class FileUpload {
    private byte[] content;
    private String fileName;
    private String extension;
    private String contentType;
    private String fieldName;
    private Map<String, Object> metadata = new HashMap<>();

    public FileUpload() { }

    public byte[] getContent() { return content; }
    public void setContent(byte[] content) { this.content = content; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getExtension() { return extension; }
    public void setExtension(String extension) { this.extension = extension; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}