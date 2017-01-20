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
import javax.ws.rs.core.Response;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Path("/persons")
public class WebService {

    @GET
    @Path("/{param}")
    public Response getPersons(@PathParam("param") String msg) {

        String output = "Person outPut : " + msg;

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
        try {
            //show the report
            report.show();

            //export the report to a pdf file
            report.toPdf(new FileOutputStream("C:\\tmp\\report.pdf"));
        } catch (DRException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return Response.status(200).entity(output).build();

    }

 }