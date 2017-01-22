package ru.glukhov.rest;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.query.JRXPathQueryExecuterFactory;
import net.sf.jasperreports.engine.util.JRXmlUtils;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Path("/statement")
public class WebService {

    @GET
    @Path("/{param}")
    @Produces("application/pdf")
    public Response getPersons(@PathParam("param") String snils) {
        System.out.println(snils);
        Response.ResponseBuilder response = null;


        Date endDate =new Date();
        try {
            XML content = getAccountStatement(snils, endDate);
            if (content != null) {
                File print = print(content);
                response = Response.ok((Object) print);
                response.header("Content-Disposition",
                        "attachment; filename=1.pdf");

            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }



        /*File file = new File("C:\\tmp\\options_sorento.pdf");
        response = Response.ok((Object) file);
        response.header("Content-Disposition",
                "attachment; filename=options_sorento.pdf");
        //return Response.status(200).entity(output).build();*/
        return response.build();

    }

    private File print(XML xml) throws JAXBException, IOException, JRException {
        File fileReport = getTmpFile();//\temp\2017
        byte[] bytes = xml.getClass().equals(XMLContentWrapper.class) ?
                ((XMLContentWrapper) xml).getContent().getBytes("UTF-8") :
                beanToXml(xml.getClass(), xml,"");
        File fileXmlContent = getTmpFile();
        writeFile(fileXmlContent, bytes);//\temp\2017

        String mainTemplateCode = "F-P-ACCSTT-JASPER-IN";
        File fileTemplate =new File("C:\\tmp\\AccountStatement.jasper");

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(JRXPathQueryExecuterFactory.PARAMETER_XML_DATA_DOCUMENT, JRXmlUtils.parse(fileXmlContent));
        params.put(JRXPathQueryExecuterFactory.XML_DATE_PATTERN, "dd-MM-yyyy");
        params.put(JRXPathQueryExecuterFactory.XML_NUMBER_PATTERN, "#,##0.##");
        params.put("AgentFIO", "Test");
        params.put("StatementDate", "");
        params.put("StatementDateFrom", "");

        DefaultJasperReportsContext.getInstance().setProperty(
                "net.sf.jasperreports.components.sort.up.arrow.char", "");
        DefaultJasperReportsContext.getInstance().setProperty(
                "net.sf.jasperreports.components.sort.down.arrow.char", "");

        JasperPrint print = JasperFillManager.fillReport(fileTemplate.getPath(), params);
        removeBlankPages(print);
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
        exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, fileReport.getPath());
        exporter.exportReport();

        return fileReport;
    }

    public static void removeBlankPages(JasperPrint print){
        java.util.List pages = print.getPages();
        Iterator<List> i = pages.iterator();
        while (i.hasNext()) {
            JRPrintPage page = (JRPrintPage)i.next();
            if (page.getElements().size() == 0)
                i.remove();
        }
    }

    public static void writeFile(File file, byte[] bytes) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        Throwable var3 = null;

        try {
            out.write(bytes);
        } catch (Throwable var12) {
            var3 = var12;
//            throw var12;
        } finally {
            if(out != null) {
                if(var3 != null) {
                    try {
                        out.close();
                    } catch (Throwable var11) {
                        //                      var3.addSuppressed(var11);
                    }
                } else {
                    out.close();
                }
            }

        }

    }

    public static byte[] beanToXml(Class clazz, Object bean, String encoding) throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(new Class[]{clazz});
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.valueOf(true));
        marshaller.setProperty("jaxb.encoding", encoding);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Throwable var6 = null;

        byte[] var7 = new byte[0];
        try {
            marshaller.marshal(bean, out);
            out.flush();
            var7 = out.toByteArray();
        } catch (Throwable var16) {
            var6 = var16;
            //throw var16;
        } finally {
            if(out != null) {
                if(var6 != null) {
                    try {
                        out.close();
                    } catch (Throwable var15) {
                        //          var6.addSuppressed(var15);
                    }
                } else {
                    out.close();
                }
            }

        }

        return var7;
    }


    public static File getTmpFile() {
        return getTmpFile("tmp");
    }

    public static File getTmpFile(String ext) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-SSS");
        File file = new File(getTmpDir(), dateFormat.format(new Date()) + "." + ext);
        file.deleteOnExit();
        return file;
    }
    public static String getTmpDir() {
        String tmpDir = System.getProperty("catalina.home");
        return tmpDir != null?tmpDir + System.getProperty("file.separator") + "temp":System.getProperty("java.io.tmpdir");
    }

    private XML getAccountStatement(String snils, Date endDate) throws Throwable {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String url;

        url = MessageFormat.format("{0}/{1}?SNILS={2}&toDate={3}",
                "http://nemyatov.bops.local:8080/mediator-2.0/api/ws",
                "get_account_card",
                URLEncoder.encode(reformat(snils), "UTF-8"),
                df.format(endDate));

        return new XMLContentWrapper(doGet(new URL(url), "", 15 * 1000, 60 * 1000));
    }

    public static String reformat(String insuranceNumber) {
        if (insuranceNumber == null)
            return null;
        String digits = insuranceNumber.replaceAll("\\D", "");
        if (digits.length() != 11)
            throw new IllegalArgumentException("Insurance number should consists of 11 digits");
        return MessageFormat.format("{0}-{1}-{2} {3}", digits.substring(0, 3), digits.substring(3, 6), digits.substring(6, 9), digits.substring(9, 11));
    }

    public String doGet(URL url, String cookie, int connectTimeout, int readTimeout) throws Throwable {
        return doGet(url, cookie, (HashMap)null, connectTimeout, readTimeout);
    }

    public static String doGet(URL url, String cookie, HashMap<String, String> propertyMap, int connectTimeout, int readTimeout) throws Throwable {
        URLConnection conn = url.openConnection();
        if(StringUtils.isNotBlank(cookie)) {
            conn.setRequestProperty("Cookie", cookie);
        }

        if(connectTimeout >= 0) {
            conn.setConnectTimeout(connectTimeout);
        }

        if(readTimeout >= 0) {
            conn.setReadTimeout(readTimeout);
        }

        if(propertyMap != null) {
            Iterator in = propertyMap.keySet().iterator();

            while(in.hasNext()) {
                String key = (String)in.next();
                conn.setRequestProperty(key, (String)propertyMap.get(key));
            }
        }

        InputStream in1 = conn.getInputStream();
        Throwable key1 = null;

        String var13;
        try {
            String encoding = conn.getContentEncoding();
            encoding = encoding == null?"utf-8":encoding;
            InputStreamReader inr = new InputStreamReader(in1, Charset.forName(encoding));
            StringBuilder stringBuilder = new StringBuilder();
            char[] buffer = new char[1];
            int len;
            if((len = inr.read(buffer)) != -1) {
                stringBuilder.append(buffer, 0, len);
                buffer = new char[1024];

                while((len = inr.read(buffer)) != -1) {
                    stringBuilder.append(buffer, 0, len);
                }

                var13 = stringBuilder.toString();
                return var13;
            }

            var13 = null;
        } catch (Throwable var23) {
            key1 = var23;
            throw var23;
        } finally {
            if(in1 != null) {
                if(key1 != null) {
                    try {
                        in1.close();
                    } catch (Throwable var22) {
                        //key1.addSuppressed(var22);
                    }
                } else {
                    in1.close();
                }
            }

        }

        return var13;
    }



}