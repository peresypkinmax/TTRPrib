/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tt.reports.prib;

import java.sql.ResultSet;
import java.sql.Types;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import minersinstrument.db.ADBProc;
import minersinstrument.db.ADBProcParametr;
import minersinstrument.db.PADBUtils;
import minersinstrument.db.PADBUtils.PADBResult;
import minersinstrument.ui.ADialog;
import minersinstrument.ui.AUniversalDialog;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.swing.JRViewer;
import org.postgresql.ds.PGPoolingDataSource;
import tradeterminal.Setup;
import tt.reports.prib.TTRPribDPanel;
import ttireport.TTIReport;


/**
 *
 * @author pkopychenko
 */
public class TTPrib implements TTIReport {

    private DateFormat df = DateFormat.getDateInstance();
    List<ProductGroup> pgs = new ArrayList<ProductGroup>(10);
        private class ProductGroup {
        private int groupId;
        private String groupName;

        public ProductGroup(int groupId, String groupName) {
            this.groupId = groupId;
            this.groupName = groupName;
        }

        public int getGroupId() {
            return groupId;
        }

        public String getGroupName() {
            return groupName;
        }

        @Override
        public String toString() {
            return this.groupName;
        }


    }


    public JRViewer makeReport(PGPoolingDataSource source) throws Exception {
        TTRPribDPanel p = new TTRPribDPanel(source);

        AUniversalDialog d = new AUniversalDialog(p, null, true);
        d.setTitleIcon(new javax.swing.ImageIcon(getClass().getResource("/tradeterminal/icons/TT_icons/32X32/reports/12.png")));
        d.setVisible(true);
        d.dispose();
        
        
        if (d.getReturnStatus() == ADialog.RET_OK) {
            
         
            ADBProc proc = new ADBProc("rpt_prib_v11");
            proc.addInParametr(
                    new ADBProcParametr(Types.DATE,
                    p.getBeginDate()));

            proc.addInParametr(
                    new ADBProcParametr(Types.DATE,
                    p.getEndDate()));        
        
            proc.addInParametr(
                    new ADBProcParametr(Types.INTEGER,
                    p.getGroupId()));

            //PADBUtils.executeVoidProcedure(Setup.getSource(), proc);

            PADBResult result = PADBUtils.getResultSet(source, proc);


            JasperReport jasperReport =
                    JasperCompileManager.compileReport(
                    getClass().getResource("/tt/reports/prib/prib.jrxml").openStream());


            HashMap map = new HashMap();
            map.put("REPORT_PARAMETRS_TEXT",
                    " с " + df.format(p.getBeginDate()) + " по " + df.format(p.getEndDate()) + ".");
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map,
                    new JRResultSetDataSource(result.getRs()));

            result.close();

            return new JRViewer(jasperPrint);
        } else {
            return null;
        }
    }
}
