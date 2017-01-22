package ru.glukhov.rest;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.column.Columns;
import net.sf.dynamicreports.report.builder.component.Components;
import net.sf.dynamicreports.report.builder.datatype.DataTypes;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.exception.DRException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Path("/statement")
public class WebService {

    @GET
    @Path("/{param}")
    @Produces("application/pdf")
    public Response getPersons(@PathParam("param") String msg) {
        System.out.println(msg);
        //String output = "Person outPut : " + msg;

        Response.ResponseBuilder response = null;

        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/postgres","postgres", "postgres");
        } catch (SQLException e) {
            e.printStackTrace();
         } catch (ClassNotFoundException e) {
            e.printStackTrace();

        }

        JasperReportBuilder report = DynamicReports.report();//a new report
        report
                .columns(
                        Columns.column("Customer Id", "id", DataTypes.integerType()),
                        Columns.column("First Name", "fio", DataTypes.stringType()))
                                 .title(//title of the report
                        Components.text("SimpleReportExample")
                                .setHorizontalAlignment(HorizontalAlignment.CENTER))
                .pageFooter(Components.pageXofY())//show page number on the page footer
                .setDataSource("SELECT id, fio" +
                                "  FROM public.clients",
                        connection);


        File file = new File("C:\\tmp\\options_sorento.pdf");
        response = Response.ok((Object) file);
        response.header("Content-Disposition",
                "attachment; filename=options_sorento.pdf");


        //return Response.status(200).entity(output).build();
        return response.build();

    }

 }