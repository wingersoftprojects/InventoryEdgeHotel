
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author bajuna
 */
@ManagedBean
@SessionScoped
public class GuestFolioBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private List<GuestFolio> GuestFolios;
    private String ActionMessage = null;
    private String SearchGuestFolioNames = "";
    private GuestFolio SelectedGuestFolio;
    private GuestFolio SelectedBillGuestFolio;
    private GuestFolio SelectedSchemeGuestFolio;
    private List<GuestFolio> GuestFolioObjectList;
    private List<GuestFolio> ReportGuestFolios = new ArrayList<>();
    
    List<GuestFolio> ReportGuestFolioSummary = new ArrayList<>();
    
    public List<GuestFolio> getReportGuestFolios() {
        return ReportGuestFolios;
    }
    
    public void setReportGuestFolios(List<GuestFolio> ReportGuestFolios) {
        this.ReportGuestFolios = ReportGuestFolios;
    }
    
    public List<GuestFolio> getReportGuestFolioSummary() {
        return ReportGuestFolioSummary;
    }
    
    public void setReportGuestFolioSummary(List<GuestFolio> ReportGuestFolioSummary) {
        this.ReportGuestFolioSummary = ReportGuestFolioSummary;
    }
    
    public GuestFolio findGuestFolio(Long GuestFolioId) {
        String sql = "{call sp_search_guest_folio_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, GuestFolioId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return this.getGuestFolioFromResultSet(rs);
            } else {
                return null;
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            return null;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
    }
    
    public void saveGuestFolio(GuestFolio guest_folio) {
        String sql = null;
        String msg = "";
        String sql2 = null;
        if (guest_folio.getTransactor() != null) {
            sql2 = "SELECT * FROM guest_folio WHERE transactor_id=" + guest_folio.getTransactor().getTransactorId() + " and is_current='Y'";
        } else {
            sql2 = "SELECT * FROM guest_folio WHERE transactor_id=0 and is_current='Y'";
        }
        UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights = new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb = new GroupRightBean();
        
        if (guest_folio.getTransactor() == null) {
            msg = "GUEST/COMPANY NOT SELECTED";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (guest_folio.getStartDate() == null) {
            msg = "START DATE IS A REQUIRED FIELD";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (guest_folio.getStartDate() == null) {
            msg = "END DATE IS A REQUIRED FIELD";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if ("GUESTFOLIO".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) && guest_folio.getGuestFolioId() == 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "GUESTFOLIO", "Add") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if ("GUESTFOLIO".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) && guest_folio.getGuestFolioId() > 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "GUESTFOLIO", "Edit") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if ((new CustomValidator().CheckRecords(sql2) > 0 && guest_folio.getGuestFolioId() == 0) || (guest_folio.getGuestFolioId() > 0)) {
            msg = "An active Guest Folio for (" + guest_folio.getTransactor().getTransactorNames() + ") already exists!";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else {
            
            if (guest_folio.getGuestFolioId() == 0) {
                sql = "{call sp_insert_guest_folio(?,?,?,?,?,?)}";
            } else if (guest_folio.getGuestFolioId() > 0) {
                sql = "{call sp_update_guest_folio(?,?,?,?,?,?,?,?,?)}";
            }
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    CallableStatement cs = conn.prepareCall(sql);) {
                if (guest_folio.getGuestFolioId() == 0) {

                    //IN in_transactor_id bigint,IN in_start_date date,IN in_end_date date,IN in_add_user_detail_id int
                    cs.setLong("in_transactor_id", guest_folio.getTransactor().getTransactorId());
                    try {
                        cs.setDate("in_start_date", new java.sql.Date(guest_folio.getStartDate().getTime()));
                    } catch (NullPointerException npe) {
                        cs.setDate("in_start_date", null);
                    }
                    try {
                        cs.setDate("in_end_date", new java.sql.Date(guest_folio.getEndDate().getTime()));
                    } catch (NullPointerException npe) {
                        cs.setDate("in_end_date", null);
                    }
                    try {
                        cs.setInt("in_children", guest_folio.getChildren());
                    } catch (NullPointerException npe) {
                        cs.setDate("in_children", null);
                    }
                    try {
                        cs.setInt("in_adults", guest_folio.getAdults());
                    } catch (NullPointerException npe) {
                        cs.setDate("in_adults", null);
                    }
                    
                    cs.setInt("in_add_user_detail_id", new GeneralUserSetting().getCurrentUser().getUserDetailId());
                    cs.executeUpdate();
                    this.setActionMessage("Saved Successfully");
                    this.clearGuestFolio(guest_folio);
                    
                } else if (guest_folio.getGuestFolioId() > 0) {
                    //IN in_guest_folio_id bigint,IN in_is_current varchar(1),IN in_status varchar(20) ,IN in_transactor_id bigint,IN in_start_date date,IN in_end_date date,IN in_edit_user_detail_id int
                    cs.setLong("in_guest_folio_id", guest_folio.getGuestFolioId());
                    cs.setString("in_is_current", guest_folio.getIsCurrent());
                    cs.setString("in_status", guest_folio.getStatus());
                    cs.setLong("in_transactor_id", guest_folio.getTransactorId());
                    try {
                        cs.setDate("in_start_date", new java.sql.Date(guest_folio.getStartDate().getTime()));
                    } catch (NullPointerException npe) {
                        cs.setDate("in_start_date", null);
                    }
                    try {
                        cs.setDate("in_end_date", new java.sql.Date(guest_folio.getEndDate().getTime()));
                    } catch (NullPointerException npe) {
                        cs.setDate("in_end_date", null);
                    }
                    
                    try {
                        cs.setInt("in_children", guest_folio.getChildren());
                    } catch (NullPointerException npe) {
                        cs.setDate("in_children", null);
                    }
                    try {
                        cs.setInt("in_adults", guest_folio.getAdults());
                    } catch (NullPointerException npe) {
                        cs.setDate("in_adults", null);
                    }
                    
                    cs.setInt("in_edit_user_detail_id", new GeneralUserSetting().getCurrentUser().getUserDetailId());
                    cs.executeUpdate();
                    this.setActionMessage("Saved Successfully");
                    this.clearGuestFolio(guest_folio);
                }
            } catch (SQLException se) {
                System.err.println(se.getMessage());
                this.setActionMessage("GuestFolio NOT saved");
                FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage("GuestFolio NOT saved!"));
            }
        }
    }
    
    public GuestFolio getGuestFolioFromResultSet(ResultSet rs) {
        GuestFolio guest_folio = new GuestFolio();
        try {
            guest_folio.setGuestFolioId(rs.getLong("guest_folio_id"));
        } catch (Exception ex) {
            guest_folio.setGuestFolioId(0);
        }
        try {
            guest_folio.setTransactorId(rs.getLong("transactor_id"));
        } catch (Exception ex) {
            guest_folio.setIsCurrent("");
        }
        try {
            guest_folio.setIsCurrent(rs.getString("is_current"));
        } catch (Exception ex) {
            guest_folio.setIsCurrent("");
        }
        try {
            guest_folio.setStatus(rs.getString("status"));
        } catch (Exception ex) {
            guest_folio.setStatus("");
        }
        try {
            guest_folio.setStartDate(rs.getDate("start_date"));
        } catch (Exception ex) {
            guest_folio.setStatus(null);
        }
        try {
            guest_folio.setEndDate(rs.getDate("end_date"));
        } catch (Exception ex) {
            guest_folio.setStatus(null);
        }
        try {
            guest_folio.setTotal_amount_credit(rs.getInt("amount_credit"));
        } catch (Exception ex) {
            guest_folio.setTotal_amount_credit(0);
        }
        try {
            guest_folio.setTotal_amount_debit(rs.getInt("amount_debit"));
        } catch (Exception ex) {
            guest_folio.setTotal_amount_debit(0);
        }
        try {
            guest_folio.setChildren(rs.getInt("children"));
        } catch (Exception ex) {
            guest_folio.setChildren(0);
        }
        try {
            guest_folio.setAdults(rs.getInt("adults"));
        } catch (Exception ex) {
            guest_folio.setAdults(0);
        }
        return guest_folio;
    }
    
    private GuestFolio guestFolio;

    public GuestFolio getGuestFolio() {
        return guestFolio;
    }
    
    public void setGuestFolio(GuestFolio guestFolio) {
        this.guestFolio = guestFolio;
    }

    public void set_guest_folio(long GuestFolioId) {
        this.guestFolio = getGuestFolio(GuestFolioId);
    }
    
    public void set_guest_folio_null(){
        GuestFolio gf=new GuestFolio();
        gf.setGuestFolioId(0);
        this.guestFolio=gf;
    }
    
    public GuestFolio getGuestFolio(long GuestFolioId) {
        String sql = "{call sp_search_guest_folio_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, GuestFolioId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return this.getGuestFolioFromResultSet(rs);
            } else {
                return null;
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            return null;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
        
    }
    
    public void deleteGuestFolio(GuestFolio guest_folio) {
        String sql = "DELETE FROM guest_folio WHERE guest_folio_id=?";
        String msg = "";
        UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights = new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb = new GroupRightBean();
        
        if ("GUESTFOLIO".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "CUSTOMER", "Delete") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else {
            
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setLong(1, guest_folio.getGuestFolioId());
                ps.executeUpdate();
                this.setActionMessage("Deleted Successfully!");
                this.clearGuestFolio(guest_folio);
            } catch (SQLException se) {
                System.err.println(se.getMessage());
                this.setActionMessage("GuestFolio NOT deleted");
            }
        }
    }
    
    public void displayGuestFolio(GuestFolio GuestFolioFrom, GuestFolio GuestFolioTo) {
        GuestFolioTo.setGuestFolioId(GuestFolioFrom.getGuestFolioId());
        GuestFolioTo.setTransactorId(GuestFolioFrom.getTransactorId());
        GuestFolioTo.setStartDate(GuestFolioFrom.getStartDate());
        GuestFolioTo.setEndDate(GuestFolioFrom.getEndDate());
        GuestFolioTo.setIsCurrent(GuestFolioFrom.getIsCurrent());
        GuestFolioTo.setStatus(GuestFolioFrom.getStatus());
    }
    
    public void closeGuestFolio(GuestFolio GuestFolioFrom, GuestFolio GuestFolioTo) {
//        GuestFolioTo.setGuestFolioId(GuestFolioFrom.getGuestFolioId());
//        GuestFolioTo.setTransactorId(GuestFolioFrom.getTransactorId());
//        GuestFolioTo.setStartDate(GuestFolioFrom.getStartDate());
//        GuestFolioTo.setEndDate(GuestFolioFrom.getEndDate());
//        GuestFolioTo.setIsCurrent(GuestFolioFrom.getIsCurrent());
//        GuestFolioTo.setStatus(GuestFolioFrom.getStatus());

        String sql = "{call sp_update_close_guest_folio(?,?)}";
        
        try (Connection conn = DBConnection.getMySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
            cs.setLong(1, GuestFolioFrom.getGuestFolioId());
            cs.setLong(2, new GeneralUserSetting().getCurrentUser().getUserDetailId());
            cs.executeUpdate();
        } catch (SQLException se) {
            System.err.println(se.getMessage());
        }
        
    }
    
    public void clearGuestFolio(GuestFolio guest_folio) {
        if (guest_folio != null) {
            guest_folio.setGuestFolioId(0);
            guest_folio.setIsCurrent("");
            guest_folio.setStatus("");
            guest_folio.setStartDate(null);
            guest_folio.setEndDate(null);
            guest_folio.setChildren(null);
            guest_folio.setAdults(null);
        }
    }
    
    public void initClearGuestFolio(GuestFolio guest_folio) {
        if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
            // Skip ajax requests.
        } else if (guest_folio != null) {
            guest_folio.setGuestFolioId(0);
            guest_folio.setIsCurrent("");
            guest_folio.setStatus("");
            guest_folio.setEndDate(null);
            guest_folio.setEndDate(null);
        }
    }
    
    public void clearSelectedGuestFolio() {
        this.clearGuestFolio(this.getSelectedGuestFolio());
    }
    
    public void clearSelectedBillGuestFolio() {
        this.clearGuestFolio(this.getSelectedBillGuestFolio());
    }
    
    public List<GuestFolio> getGuestFolios() {
        String sql;
        sql = "{call sp_search_guest_folio_by_name(?)}";
        ResultSet rs = null;
        GuestFolios = new ArrayList<GuestFolio>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, this.getSearchGuestFolioNames());
            rs = ps.executeQuery();
            while (rs.next()) {
                GuestFolios.add(this.getGuestFolioFromResultSet(rs));
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
        return GuestFolios;
    }
    
    public List<GuestFolio> getGuestFoliosByTransactor_Amounts(Transactor transactor, Transactor billOther) {
        String sql;
        sql = "{call sp_report_current_guest_folio_summary_by_transactor(?)}";
        ResultSet rs = null;
        List<GuestFolio> NewGuestFolios = new ArrayList<GuestFolio>();
        if (transactor != null || billOther != null) {
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                if (billOther != null) {
                    ps.setLong(1, billOther.getTransactorId());
                } else {
                    ps.setLong(1, transactor.getTransactorId());
                }
                rs = ps.executeQuery();
                while (rs.next()) {
                    NewGuestFolios.add(this.getGuestFolioFromResultSet(rs));
                }
            } catch (SQLException se) {
                System.err.println(se.getMessage());
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException ex) {
                        System.err.println(ex.getMessage());
                    }
                }
            }
        }
        return NewGuestFolios;
    }
    
    public List<GuestFolio> getGuestFoliosByTransactor(Transactor transactor, Transactor billOther) {
        String sql;
        sql = "{call sp_search_guest_folio_by_transactor(?)}";
        ResultSet rs = null;
        List<GuestFolio> NewGuestFolios = new ArrayList<GuestFolio>();
        if (transactor != null || billOther != null) {
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                if (billOther != null) {
                    ps.setLong(1, billOther.getTransactorId());
                } else {
                    ps.setLong(1, transactor.getTransactorId());
                }
                rs = ps.executeQuery();
                while (rs.next()) {
                    NewGuestFolios.add(this.getGuestFolioFromResultSet(rs));
                }
            } catch (SQLException se) {
                System.err.println(se.getMessage());
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException ex) {
                        System.err.println(ex.getMessage());
                    }
                }
            }
        }
        return NewGuestFolios;
    }
    
    public void setGuestFolios(List<GuestFolio> GuestFolios) {
        this.GuestFolios = GuestFolios;
    }

    /**
     * @param Query
     * @return the GuestFolioStringList
     */
    public List<String> getGuestFolioStringList(String Query) {
        String sql;
        sql = "{call sp_search_guest_folio_by_name(?)}";
        ResultSet rs = null;
        List<String> GuestFolioStringList = new ArrayList<String>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, Query);
            rs = ps.executeQuery();
            while (rs.next()) {
                GuestFolioStringList.add(rs.getString("guest_folio_names"));
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
        return GuestFolioStringList;
    }

    /**
     * @param Query
     * @return the GuestFolioObjectList
     */
    public List<GuestFolio> getGuestFolioObjectList(String Query) {
        String sql;
        sql = "{call sp_search_guest_folio_by_name(?)}";
        ResultSet rs = null;
        GuestFolioObjectList = new ArrayList<GuestFolio>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, Query);
            rs = ps.executeQuery();
            while (rs.next()) {
                GuestFolioObjectList.add(this.getGuestFolioFromResultSet(rs));
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
        return GuestFolioObjectList;
    }
    
    public List<GuestFolio> getReportGuestFolios(GuestFolio aGuestFolio, boolean RETRIEVE_REPORT) {
        String sql = "{call sp_report_guest_folio(?)}";
        ResultSet rs = null;
        this.ReportGuestFolios.clear();
        if (aGuestFolio != null && RETRIEVE_REPORT == true) {
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setLong(1, aGuestFolio.getTransactorId());
                rs = ps.executeQuery();
                while (rs.next()) {
                    this.ReportGuestFolios.add(this.getGuestFolioFromResultSet(rs));
                }
            } catch (SQLException se) {
                System.err.println(se.getMessage());
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException ex) {
                        System.err.println(ex.getMessage());
                    }
                }
            }
        } else {
            this.ReportGuestFolios.clear();
        }
        return this.ReportGuestFolios;
    }
    
    public long getReportGuestFoliosCount() {
        return this.ReportGuestFolios.size();
    }

    /**
     * @param GuestFolioObjectList the GuestFolioObjectList to set
     */
    public void setGuestFolioObjectList(List<GuestFolio> GuestFolioObjectList) {
        this.GuestFolioObjectList = GuestFolioObjectList;
    }

    /**
     * @return the SearchGuestFolioNames
     */
    public String getSearchGuestFolioNames() {
        return SearchGuestFolioNames;
    }

    /**
     * @param SearchGuestFolioNames the SearchGuestFolioNames to set
     */
    public void setSearchGuestFolioNames(String SearchGuestFolioNames) {
        this.SearchGuestFolioNames = SearchGuestFolioNames;
    }

    /**
     * @return the ActionMessage
     */
    public String getActionMessage() {
        return ActionMessage;
    }

    /**
     * @param ActionMessage the ActionMessage to set
     */
    public void setActionMessage(String ActionMessage) {
        this.ActionMessage = ActionMessage;
    }

    /**
     * @return the SelectedGuestFolio
     */
    public GuestFolio getSelectedGuestFolio() {
        return SelectedGuestFolio;
    }

    /**
     * @param SelectedGuestFolio the SelectedGuestFolio to set
     */
    public void setSelectedGuestFolio(GuestFolio SelectedGuestFolio) {
        this.SelectedGuestFolio = SelectedGuestFolio;
    }

    /**
     * @return the SelectedBillGuestFolio
     */
    public GuestFolio getSelectedBillGuestFolio() {
        return SelectedBillGuestFolio;
    }

    /**
     * @param SelectedBillGuestFolio the SelectedBillGuestFolio to set
     */
    public void setSelectedBillGuestFolio(GuestFolio SelectedBillGuestFolio) {
        this.SelectedBillGuestFolio = SelectedBillGuestFolio;
    }

    /**
     * @return the SelectedSchemeGuestFolio
     */
    public GuestFolio getSelectedSchemeGuestFolio() {
        return SelectedSchemeGuestFolio;
    }

    /**
     * @param SelectedSchemeGuestFolio the SelectedSchemeGuestFolio to set
     */
    public void setSelectedSchemeGuestFolio(GuestFolio SelectedSchemeGuestFolio) {
        this.SelectedSchemeGuestFolio = SelectedSchemeGuestFolio;
    }
    
    public List<GuestFolio> getReportCurrentGuestFolioSummaryByTransactor(long aBillTransactorId) {
        String sql;
        sql = "{call sp_report_current_guest_folio_summary_by_transactor(?)}";
        ResultSet rs = null;
        this.ReportGuestFolioSummary.clear();
        if (aBillTransactorId != 0) {
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                try {
                    ps.setLong(1, aBillTransactorId);
                } catch (NullPointerException npe) {
                    ps.setLong(1, 0);
                }
                rs = ps.executeQuery();
                //System.out.println(rs.getStatement());
                GuestFolio TmpGuestFolio;
                while (rs.next()) {
                    TmpGuestFolio = new GuestFolio();
                    try {
                        TmpGuestFolio.setTotal_amount_credit(rs.getInt("amount_credit"));
                    } catch (Exception ex) {
                        TmpGuestFolio.setTotal_amount_credit(0);
                    }
                    try {
                        TmpGuestFolio.setTotal_amount_debit(rs.getInt("amount_debit"));
                    } catch (Exception ex) {
                        TmpGuestFolio.setTotal_amount_debit(0);
                    }
                    try {
                        TmpGuestFolio.setStartDate(rs.getDate("start_date"));
                    } catch (Exception ex) {
                        TmpGuestFolio.setStartDate(null);
                    }
                    try {
                        TmpGuestFolio.setEndDate(rs.getDate("end_date"));
                    } catch (Exception ex) {
                        TmpGuestFolio.setEndDate(null);
                    }
                    this.ReportGuestFolioSummary.add(TmpGuestFolio);
                }
                //this.ActionMessage=((""));
            } catch (SQLException se) {
                System.err.println(se.getMessage());
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException ex) {
                        System.err.println(ex.getMessage());
                    }
                }
            }
        }
        return this.ReportGuestFolioSummary;
    }
}
