package mg.itu.mvc.bind;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import mg.itu.mvc.file.FileUpload;

import java.io.InputStream;
import java.lang.reflect.Parameter;

public class ParameterBinder {

    public static FileUpload bindFileUpload(HttpServletRequest request, Parameter parameter)
            throws Exception {
        Part part = findMatchingPart(request, parameter);

        if (part == null || part.getSubmittedFileName() == null
                || part.getSubmittedFileName().isBlank()) {
            return null;
        }

        byte[] content;
        try (InputStream in = part.getInputStream()) {
            content = in.readAllBytes();
        }

        String fileName    = part.getSubmittedFileName();
        String contentType = part.getContentType();
        String fieldName   = part.getName();
        String extension   = extractExtension(fileName);

        FileUpload fu = new FileUpload();
        fu.setContent(content);
        fu.setFileName(fileName);
        fu.setContentType(contentType);
        fu.setFieldName(fieldName);
        fu.setExtension(extension);
        fu.getMetadata().put("size", (long) content.length);

        return fu;
    }

    private static Part findMatchingPart(HttpServletRequest request, Parameter parameter)
            throws Exception {
        String expected = parameter.getName();
        for (Part p : request.getParts()) {
            if (p.getSubmittedFileName() != null
                    && (expected.equals(p.getName()) || p.getName().equals("filemap"))) {
                return p;
            }
        }
        return null;
    }

    public static String extractExtension(String fileName) {
        if (fileName == null) return "";
        int dot = fileName.lastIndexOf('.');
        return (dot == -1) ? "" : fileName.substring(dot + 1).toLowerCase();
    }
}