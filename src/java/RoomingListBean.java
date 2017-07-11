
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
public class RoomingListBean {

    private List<RoomingList> roomingList;
    private List<ReservionList> reservionList;
    
    private List<RoomingList> filteredRL;

    public List<RoomingList> getFilteredRL() {
        return filteredRL;
    }

    public void setFilteredRL(List<RoomingList> filteredRL) {
        this.filteredRL = filteredRL;
    }

    public List<RoomingList> roominglist() {
        roomingList = new ArrayList<>();
        String sql = "SELECT ro.room_occupancy_id,\n"
                + "r.room_number AS `RM No.`,\n"
                + "rc.room_category_name AS `RM TYPE`,\n"
                + "tr.transactor_names AS `GUEST NAME`,\n"
                + "CONCAT(CASE WHEN t_bill.transactor_names=tr.transactor_names THEN 'SELF' WHEN t_bill.transactor_names IS NULL THEN '' ELSE 'ORG' END,(CASE WHEN ro.transactor_id<>t.bill_transactor_id THEN CONCAT('/',(SELECT transactor_names from transactor where transactor_id=t.bill_transactor_id)) ELSE '' END)) AS `ORG/SELF`,\n"
                + "ro.start_date AS `CHECK-IN DATE`,\n"
                + "ro.end_date AS `CHECK-OUT DATE`,\n"
                + "t.number_of_persons AS PAX,\n"
                + "ro.room_occupancy_status,ro.remarks,t.transaction_id,\n"
                + "(SELECT unit_price from transaction_item where transaction_id=t.transaction_id LIMIT 1) AS RATE,\n"
                + "(SELECT item_id from transaction_item where transaction_id=t.transaction_id LIMIT 1) AS item_id \n"
                + "FROM\n"
                + "view_room_occupancy AS ro\n"
                + "RIGHT JOIN `transaction` AS t ON ro.transaction_id = t.transaction_id\n"
                + "RIGHT JOIN room AS r ON ro.room_id = r.room_id\n"
                + "LEFT JOIN room_package AS rp ON ro.room_package_id = rp.room_package_id\n"
                + "INNER JOIN room_category AS rc ON r.room_category_id = rc.room_category_id\n"
                + "LEFT JOIN transactor AS tr ON ro.transactor_id = tr.transactor_id\n"
                + "LEFT JOIN transactor AS t_bill ON t.bill_transactor_id = t_bill.transactor_id"
                + " where actual_exit_date is null ORDER BY r.room_number";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            rs = ps.executeQuery();
            while (rs.next()) {
                RoomingList r = new RoomingList();
                r.setRoomOccupancyId(rs.getInt("room_occupancy_id"));
                r.setRoomNumber(rs.getString("RM No."));
                r.setRoomType(rs.getString("RM TYPE"));
                r.setGuestName(rs.getString("GUEST NAME"));
                r.setOrg_Self(rs.getString("ORG/SELF"));
                r.setStartDate(rs.getDate("CHECK-IN DATE"));
                r.setEndDate(rs.getDate("CHECK-OUT DATE"));
                r.setNumberOfPeople(rs.getInt("PAX"));
                r.setRoomOccupancyStatus(rs.getString("room_occupancy_status"));
                r.setRemarks(rs.getString("remarks"));
                r.setTransaction_id(rs.getInt("transaction_id"));
                r.setRate(rs.getFloat("RATE"));
                r.setItem_id(rs.getInt("item_id"));
                roomingList.add(r);
            }
            return roomingList;
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            return new ArrayList<>();
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

    public List<RoomingList> getRoomingList() {
        //if (roomingList == null || roomingList.size()==0) {
        roomingList = roominglist();
        //}
        return roomingList;
        //return roomingList;
    }

    public void setRoomingList(List<RoomingList> roomingList) {
        this.roomingList = roomingList;
    }

    public List<ReservionList> getReservionList() {
        reservionList = new ArrayList<>();
        String sql = "SELECT `transaction`.transaction_id,"
                + " tr.transaction_reason_name,\n"
                + "transactor.transactor_names,\n"
                + "`transaction`.start_date,\n"
                + "`transaction`.end_date,\n"
                + "`transaction`.number_of_persons,\n"
                + "`transaction`.reserved_by,\n"
                + "`transaction`.terms_conditions,\n"
                + "`transaction`.transaction_status FROM\n"
                + "`transaction`\n"
                + "INNER JOIN transactor ON `transaction`.transactor_id = transactor.transactor_id \n"
                + "INNER JOIN transaction_reason tr ON tr.transaction_reason_id=`transaction`.transaction_reason_id"
                + " where `transaction`.transaction_type_id=14 and start_date>=CURDATE()"
                + " AND transaction_status='Reserved' order by start_date";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            rs = ps.executeQuery();
            while (rs.next()) {
                ReservionList r = new ReservionList();
                r.setTransactorName(rs.getString("transactor_names"));
                r.setReservedBy(rs.getString("reserved_by"));
                r.setTerms(rs.getString("terms_conditions"));
                r.setStartDate(rs.getDate("start_date"));
                r.setEndDate(rs.getDate("end_date"));
                r.setNumberOfPersons(rs.getInt("number_of_persons"));
                r.setTransactionReasonName(rs.getString("transaction_reason_name"));
                r.setTransactionStatus(rs.getString("transaction_status"));
                r.setTransactionId(rs.getInt("transaction_id"));
                reservionList.add(r);
            }
            return reservionList;
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            return new ArrayList<>();
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

    public void setReservionList(List<ReservionList> reservionList) {
        this.reservionList = reservionList;
    }

    public void ChangeRoomOccupancyStatus(int room_occupancy_id, String status) {
        String sql = "";
        if (status.equals("Cancel")) {
            sql = "UPDATE room_occupancy set room_occupancy_status='Cancelled',actual_exit_date=NOW(),edit_date=NOW(),check_out_date=NOW(), edit_user_detail_id=" + new GeneralUserSetting().getCurrentUser().getUserDetailId() + " where room_occupancy_id=" + room_occupancy_id;
        } else if (status.equals("Check Out")) {
            sql = "UPDATE room_occupancy set room_occupancy_status='Checked Out',actual_exit_date=NOW(),check_out_date=NOW(), check_out_user_detail_id=" + new GeneralUserSetting().getCurrentUser().getUserDetailId() + " where room_occupancy_id=" + room_occupancy_id;
        }

        try (
                Connection conn = DBConnection.getMySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
            cs.executeUpdate();
            FacesContext context = FacesContext.getCurrentInstance();
            if (status.equals("Cancel")) {
                context.addMessage(null, new FacesMessage("Cancelled Successfuly", "Cancelled Successfuly"));
            } else if (status.equals("Check Out")) {
                context.addMessage(null, new FacesMessage("Checked Out Successfuly", "Checked Out Successfuly"));
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("An Error Occured", "An Error Occured"));
        }
        roomingList = roominglist();
    }

    public void EditRemarks(int room_occupancy_id, String remarks) {
        String sql = "";
        sql = "UPDATE room_occupancy set remarks='" + remarks + "' where room_occupancy_id=" + room_occupancy_id;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
            cs.executeUpdate();
//            FacesContext context = FacesContext.getCurrentInstance();
//            if (status.equals("Cancel")) {
//                context.addMessage(null, new FacesMessage("Cancelled Successfuly", "Cancelled Successfuly"));
//            } else if (status.equals("Check Out")) {
//                context.addMessage(null, new FacesMessage("Checked Out Successfuly", "Checked Out Successfuly"));
//            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("An Error Occured", "An Error Occured"));
        }
        roomingList = roominglist();
    }

    public void ChangeTransactionStatus(int transaction_id) {
        String sql = "";
        sql = "UPDATE `transaction` set transaction_status='Cancelled',edit_date=NOW(), edit_user_detail_id=" + new GeneralUserSetting().getCurrentUser().getUserDetailId() + " where transaction_id=" + transaction_id;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
            cs.executeUpdate();
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Cancelled Successfuly", "Cancelled Successfuly"));
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("An Error Occured", "An Error Occured"));
        }
    }

}
