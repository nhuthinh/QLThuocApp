package gui;

import controller.ThuocController;
import controller.HoaDonController;
import entities.ChiTietHoaDon;
import entities.HoaDon;
import entities.KhachHang;
import entities.Thuoc;
import utils.MessageDialog;
import controller.KhachHangController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.awt.Dimension;


public class AddHoaDonDialog extends JDialog {
	private HoaDonController hoaDonController = new HoaDonController();
	private JTextField txtDiemSuDung;
	private JLabel lblDiemHienTai;
	private JLabel lblThanhTienSauGiam;
	private int diemHienTai = 0;  // tích điểm hiện tại của khách
    private JTextField txtIdHD, txtThoiGian, txtIdNV, txtPhuongThuc, txtTrangThai, txtTongTien;
    private JComboBox<String> cbKhachHang;
    private List<String> danhSachKhach;
    private JTable tblThuoc;
    private DefaultTableModel modelThuoc;
    private JButton btnAddThuoc, btnDeleteThuoc, btnSave, btnCancel;
    private boolean saved = false;
    private int diemSuDung = 0;
    private boolean isUpdatingTable = false;
    private double thanhTienSauGiam = 0.0;
    private List<Thuoc> allThuocList = new ArrayList<>();
    private ThuocController thuocController = new ThuocController();
    private KhachHangController khachHangController = new KhachHangController();


    public AddHoaDonDialog(JFrame parent) {
        super(parent, "Thêm hóa đơn mới", true);
        setPreferredSize(new Dimension(1600, 800));
        setSize(750, 550);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        allThuocList = thuocController.getAllThuoc();

        // ====== Panel thông tin hóa đơn ======
        JPanel pnlInfo = new JPanel(new GridLayout(3, 4, 8, 8));
        pnlInfo.setBorder(BorderFactory.createTitledBorder("Thông tin hóa đơn"));

        txtIdHD = new JTextField();
        txtThoiGian = new JTextField();
        txtIdNV = new JTextField();
        txtIdHD.setText(hoaDonController.getNextHoaDonId());
        txtIdHD.setEditable(false); // Không cho sửa tay

        lblDiemHienTai = new JLabel("Điểm hiện có: 0");
        txtDiemSuDung = new JTextField("0");
        txtDiemSuDung.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                capNhatThanhTienSauGiam();
            }
        });
        lblThanhTienSauGiam = new JLabel("Thanh toán: 0");
        txtPhuongThuc = new JTextField();
        txtTrangThai = new JTextField();
        txtTongTien = new JTextField("0");
        txtTongTien.setEditable(false);

        // Thời gian thực (hiện tại) không cho nhập tay
        txtThoiGian.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
        txtThoiGian.setEditable(false);
        List<KhachHang> allKhachList = khachHangController.getAllKhachHang();
        cbKhachHang = new JComboBox<>();
        cbKhachHang.setEditable(true);
        danhSachKhach = new ArrayList<>();
        for (KhachHang kh : allKhachList) {
            String s = kh.getIdKH() + " - " + kh.getHoTen() + " - " + kh.getSdt();
            danhSachKhach.add(s);
            cbKhachHang.addItem(s);
        }
        cbKhachHang.getEditor().getEditorComponent().addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                String input = ((JTextField) cbKhachHang.getEditor().getEditorComponent()).getText().trim();
                cbKhachHang.hidePopup();
                cbKhachHang.removeAllItems();
                boolean found = false;
                for (String s : danhSachKhach) {
                    if (s.toLowerCase().contains(input.toLowerCase())) {
                        cbKhachHang.addItem(s);
                        found = true;
                    }
                }
                cbKhachHang.setSelectedItem(input);
                cbKhachHang.showPopup();
            }
        });

        cbKhachHang.addActionListener(e -> {
            String selected = (String) cbKhachHang.getSelectedItem();
            String idKH = selected != null ? selected.split(" - ")[0].trim() : "";
            diemHienTai = 0;
            if (!idKH.isEmpty()) {
                try {
                    diemHienTai = khachHangController.getDiemHienTai(idKH);
                } catch (Exception ex) {
                    diemHienTai = 0;
                }
            }
            lblDiemHienTai.setText("Điểm hiện có: " + diemHienTai);
            capNhatThanhTienSauGiam();
        });




        cbKhachHang.getEditor().getEditorComponent().addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                String input = ((JTextField) cbKhachHang.getEditor().getEditorComponent()).getText().trim();
                cbKhachHang.hidePopup();
                cbKhachHang.removeAllItems();
                boolean found = false;
                for (String s : danhSachKhach) {
                    if (s.toLowerCase().contains(input.toLowerCase())) {
                        cbKhachHang.addItem(s);
                        found = true;
                    }
                }
                cbKhachHang.setSelectedItem(input);
                cbKhachHang.showPopup();
            }
        });


        pnlInfo.add(new JLabel("Mã hóa đơn:"));
        pnlInfo.add(txtIdHD);
        pnlInfo.add(new JLabel("ID Nhân viên:"));
        pnlInfo.add(txtIdNV);
        pnlInfo.add(txtThoiGian);
        pnlInfo.add(new JLabel("ID Khách hàng:"));
        JPanel khachHangPanel = new JPanel(new BorderLayout());
        khachHangPanel.add(cbKhachHang, BorderLayout.CENTER);
        pnlInfo.add(khachHangPanel);
        pnlInfo.add(lblDiemHienTai); // chỉ add 1 lần!
        pnlInfo.add(new JLabel("Điểm sử dụng:"));
        pnlInfo.add(txtDiemSuDung);
        pnlInfo.add(new JLabel("Phương thức ..."));
        pnlInfo.add(txtPhuongThuc);
        pnlInfo.add(new JLabel("Trạng thái:"));
        pnlInfo.add(txtTrangThai);

        // v.v.



        // Dòng tổng tiền riêng
        JPanel pnlTongTien = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlTongTien.add(new JLabel("Tổng tiền:"));
        pnlTongTien.add(txtTongTien);

        // ====== Bảng nhập thuốc ======
        String[] colNames = {"Tên thuốc", "Số lượng", "Đơn giá", "Thành tiền"};
        modelThuoc = new DefaultTableModel(colNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 0 || col == 1; // Chỉ cho sửa tên thuốc, số lượng
            }
        };
        tblThuoc = new JTable(modelThuoc);

        // Set combobox cho cột tên thuốc
        JComboBox<String> cbTenThuoc = new JComboBox<>();
        for (Thuoc t : allThuocList) cbTenThuoc.addItem(t.getTenThuoc());
        cbTenThuoc.setEditable(true);
        ((JTextField) cbTenThuoc.getEditor().getEditorComponent()).addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                String input = ((JTextField) cbTenThuoc.getEditor().getEditorComponent()).getText();
                cbTenThuoc.removeAllItems();
                for (Thuoc t : allThuocList) {
                    if (t.getTenThuoc().toLowerCase().contains(input.toLowerCase())) {
                        cbTenThuoc.addItem(t.getTenThuoc());
                    }
                }
                cbTenThuoc.setSelectedItem(input);
                cbTenThuoc.showPopup();
            }
        });
        tblThuoc.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(cbTenThuoc));

        // Sự kiện khi sửa bảng thuốc, sẽ tự cập nhật đơn giá/thành tiền/tổng tiền
        modelThuoc.addTableModelListener(e -> updateThanhTienVaTongTien());

        JScrollPane scrollPane = new JScrollPane(tblThuoc);

        // Nút thêm/xóa thuốc
        JPanel pnlBtnThuoc = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAddThuoc = new JButton("Thêm thuốc");
        btnDeleteThuoc = new JButton("Xóa dòng");
        pnlBtnThuoc.add(btnAddThuoc);
        pnlBtnThuoc.add(btnDeleteThuoc);

        btnAddThuoc.addActionListener(e -> modelThuoc.addRow(new Object[]{"", 1, 0.0, 0.0}));
        btnDeleteThuoc.addActionListener(e -> {
            int row = tblThuoc.getSelectedRow();
            if (row >= 0) modelThuoc.removeRow(row);
        });

        // ====== Panel nút lưu/hủy ======
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSave = new JButton("Lưu hóa đơn");
        btnCancel = new JButton("Hủy");
        pnlBtns.add(btnSave);
        pnlBtns.add(btnCancel);

        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> dispose());

        // ====== Layout tổng thể ======
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.add(pnlInfo, BorderLayout.CENTER);

        add(pnlTop, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(pnlBtnThuoc, BorderLayout.WEST);
        add(pnlTongTien, BorderLayout.SOUTH);
        add(pnlBtns, BorderLayout.PAGE_END);
    }

    // ====== Các hàm xử lý tính toán và lấy dữ liệu ======

    private void updateThanhTienVaTongTien() {
        if (isUpdatingTable) return;
        isUpdatingTable = true;

        double tongTien = 0.0;
        for (int i = 0; i < modelThuoc.getRowCount(); i++) {
            String tenThuoc = String.valueOf(modelThuoc.getValueAt(i, 0));
            int soLuong = 1;
            try {
                soLuong = Integer.parseInt(String.valueOf(modelThuoc.getValueAt(i, 1)));
                if (soLuong < 0) soLuong = 1;
            } catch (Exception ex) {
                soLuong = 1;
            }
            double donGia = 0.0;
            for (Thuoc t : allThuocList) {
                if (t.getTenThuoc().equals(tenThuoc)) {
                    donGia = t.getDonGia();
                    break;
                }
            }
            double thanhTien = soLuong * donGia;

            if (!String.valueOf(modelThuoc.getValueAt(i, 2)).equals(String.valueOf(donGia))) {
                modelThuoc.setValueAt(donGia, i, 2);
            }
            if (!String.valueOf(modelThuoc.getValueAt(i, 3)).equals(String.valueOf(thanhTien))) {
                modelThuoc.setValueAt(thanhTien, i, 3);
            }
            tongTien += thanhTien;
        }
        txtTongTien.setText(String.format("%.0f", tongTien));
        isUpdatingTable = false;
    }



    private void onSave() {
        if (txtIdHD.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã hóa đơn không được để trống!");
            return;
        }
        if (modelThuoc.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Hóa đơn cần ít nhất một loại thuốc!");
            return;
        }
        this.diemSuDung = 0;
        this.thanhTienSauGiam = 0.0;
        try {
            diemSuDung = Integer.parseInt(txtDiemSuDung.getText().trim());
            if (diemSuDung < 0) diemSuDung = 0;
            if (diemSuDung > diemHienTai) diemSuDung = diemHienTai;
        } catch (Exception ex) {
            diemSuDung = 0;
        }
        try {
            double tongTien = Double.parseDouble(txtTongTien.getText().trim());
            thanhTienSauGiam = tongTien - diemSuDung * 1000;
            if (thanhTienSauGiam < 0) thanhTienSauGiam = 0;
        } catch (Exception ex) {
            thanhTienSauGiam = 0.0;
        }
        saved = true;
        dispose(); // Đóng dialog
    }

    // ====== Getter để panel lấy dữ liệu ======
    public HoaDon getHoaDon() {
        HoaDon hd = new HoaDon();
        hd.setIdHD(txtIdHD.getText().trim());
        try {
            hd.setThoiGian(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(txtThoiGian.getText().trim()));
        } catch (Exception ex) {
            hd.setThoiGian(new Date());
        }
        hd.setIdNV(txtIdNV.getText().trim());
        String selected = (String) cbKhachHang.getSelectedItem();
        String idKH = selected != null ? selected.split(" - ")[0].trim() : "";
        hd.setIdKH(idKH);
        hd.setTongTien(this.thanhTienSauGiam); // dùng tiền thực trả
        hd.setPhuongThucThanhToan(txtPhuongThuc.getText().trim());
        hd.setTrangThaiDonHang(txtTrangThai.getText().trim());
        return hd;
    }


    public List<ChiTietHoaDon> getChiTietHoaDonList() {
        List<ChiTietHoaDon> list = new ArrayList<>();
        for (int i = 0; i < modelThuoc.getRowCount(); i++) {
            String tenThuoc = String.valueOf(modelThuoc.getValueAt(i, 0));
            String idThuoc = "";
            double donGia = 0.0;
            for (Thuoc t : allThuocList) {
                if (t.getTenThuoc().equals(tenThuoc)) {
                    idThuoc = t.getIdThuoc();
                    donGia = t.getDonGia();
                    break;
                }
            }
            int soLuong = 1;
            try {
                soLuong = Integer.parseInt(String.valueOf(modelThuoc.getValueAt(i, 1)));
                if (soLuong < 0) soLuong = 1;
            } catch (Exception ex) {
                soLuong = 1;
            }
            ChiTietHoaDon cthd = new ChiTietHoaDon();
            cthd.setIdHD(txtIdHD.getText().trim());
            cthd.setIdThuoc(idThuoc);
            cthd.setTenThuoc(tenThuoc);
            cthd.setSoLuong(soLuong);
            cthd.setDonGia(donGia);
            list.add(cthd);
        }
        return list;
    }
    public boolean isSaved() {
        return saved;
    }
    private void capNhatThanhTienSauGiam() {
        try {
            double tongTien = Double.parseDouble(txtTongTien.getText().trim());
            int diemSuDung = Integer.parseInt(txtDiemSuDung.getText().trim());
            if (diemSuDung < 0) diemSuDung = 0;
            if (diemSuDung > diemHienTai) diemSuDung = diemHienTai;
            double thanhTien = tongTien - diemSuDung * 1000; 
            if (thanhTien < 0) thanhTien = 0;
            lblThanhTienSauGiam.setText("Thanh toán: " + String.format("%.0f", thanhTien));
        } catch (Exception ex) {
            lblThanhTienSauGiam.setText("Thanh toán: 0");
        }
    }
    public int getDiemSuDung() {
        return this.diemSuDung;
    }
    public double getThanhTienSauGiam() {
        return this.thanhTienSauGiam;
    }
}
