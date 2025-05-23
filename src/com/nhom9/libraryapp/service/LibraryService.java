package com.nhom9.libraryapp.service; 

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import com.nhom9.libraryapp.dao.BookDao;
import com.nhom9.libraryapp.dao.LoanDao;
import com.nhom9.libraryapp.model.Book;
import com.nhom9.libraryapp.model.Loan;
import com.nhom9.libraryapp.model.LoanDetail; // Import lớp mới


public class LibraryService {

	private BookDao bookDao;
	private LoanDao loanDao;
	private static final int BORROW_DURATION_DAYS = 14;

	public LibraryService() {
		this.bookDao = new BookDao();
		this.loanDao = new LoanDao();
	}

	/**
	 * Tìm kiếm sách dựa trên tiêu chí.
	 */
	public List<Book> searchBooks(String title, String author, String genre) {
		try {
			return bookDao.searchBooks(title, author, genre);
		} catch (SQLException e) {
			System.err.println("Lỗi khi tìm kiếm sách: " + e.getMessage());
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	/**
	 * Lấy danh sách tất cả sách.
	 */
	public List<Book> getAllBooks() {
		try {
			return bookDao.getAllBooks();
		} catch (SQLException e) {
			System.err.println("Lỗi khi lấy tất cả sách: " + e.getMessage());
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

    /**
     * Lấy thông tin chi tiết của một cuốn sách dựa vào ID.
     */
    public Book getBookById(int bookId) throws SQLException {
        return bookDao.getBookById(bookId);
    }


	/**
	 * Xử lý nghiệp vụ mượn sách.
	 */
	public boolean borrowBook(int userId, int bookId) {
		// Nên có Transaction ở đây
		try {
			Book book = this.getBookById(bookId);
			if (book == null || book.getSoLuongConLai() <= 0) {
				System.err.println("Mượn sách thất bại: Sách không tồn tại hoặc đã hết (ID: " + bookId + ")");
				return false;
			}

			boolean quantityUpdated = bookDao.updateBookQuantity(bookId, -1);
			if (!quantityUpdated) {
				System.err.println("Mượn sách thất bại: Không thể cập nhật số lượng sách (ID: " + bookId + ")");
				return false;
			}

			Loan newLoan = new Loan();
			newLoan.setUserId(userId);
			newLoan.setBookId(bookId);
			newLoan.setNgayMuon(Date.valueOf(LocalDate.now()));
			LocalDate dueDate = LocalDate.now().plusDays(BORROW_DURATION_DAYS);
			newLoan.setNgayTraDuKien(Date.valueOf(dueDate));
			newLoan.setTrangThai("đang mượn");
			newLoan.setNgayTraThucTe(null);

			boolean loanAdded = loanDao.addLoan(newLoan);
			if (!loanAdded) {
				System.err.println("Mượn sách thất bại: Không thể tạo phiếu mượn.");
                // Rollback thủ công
                try {
                    bookDao.updateBookQuantity(bookId, 1);
                    System.out.println("Rollback thủ công: Đã tăng lại số lượng sách ID " + bookId);
                } catch (SQLException rollbackEx) {
                     System.err.println("Lỗi nghiêm trọng khi rollback số lượng sách: " + rollbackEx.getMessage());
                }
				return false;
			}
			return true;

		} catch (SQLException e) {
			System.err.println("Lỗi CSDL khi mượn sách: " + e.getMessage());
			e.printStackTrace();
			return false;
		} catch (Exception e) {
             System.err.println("Lỗi không mong muốn khi mượn sách: " + e.getMessage());
             e.printStackTrace();
             return false;
         }
	}

	/**
	 * Xử lý nghiệp vụ trả sách.
	 */
	public boolean returnBook(int loanId) {
		// Nên có Transaction ở đây
		Loan loan = null;
		boolean loanUpdated = false;
		try {
			loan = loanDao.getLoanById(loanId); // Dùng getLoanById cơ bản
			if (loan == null || !"đang mượn".equals(loan.getTrangThai())) {
				System.err.println("Trả sách thất bại: Không tìm thấy phiếu mượn hoặc sách đã được trả (ID: " + loanId + ")");
				return false;
			}

			Date returnDate = Date.valueOf(LocalDate.now());
			loanUpdated = loanDao.updateLoanStatus(loanId, "đã trả", returnDate);
			if (!loanUpdated) {
				System.err.println("Trả sách thất bại: Không thể cập nhật phiếu mượn (ID: " + loanId + ")");
				return false;
			}

			boolean quantityUpdated = bookDao.updateBookQuantity(loan.getBookId(), 1);
			if (!quantityUpdated) {
				System.err.println("Trả sách thất bại: Không thể cập nhật số lượng sách (ID: " + loan.getBookId() + ")");
                // Rollback thủ công
                try {
                     loanDao.updateLoanStatus(loanId, "đang mượn", null);
                     System.out.println("Rollback thủ công: Đã đặt lại trạng thái phiếu mượn ID " + loanId);
                 } catch (SQLException rollbackEx) {
                     System.err.println("Lỗi nghiêm trọng khi rollback phiếu mượn: " + rollbackEx.getMessage());
                 }
				return false;
			}
			return true;

		} catch (SQLException e) {
			System.err.println("Lỗi CSDL khi trả sách: " + e.getMessage());
			e.printStackTrace();
            // Rollback thủ công nếu lỗi xảy ra sau khi đã cập nhật phiếu mượn
            if (loanUpdated && loan != null) {
                 try {
                     loanDao.updateLoanStatus(loanId, "đang mượn", null);
                     System.out.println("Rollback thủ công do lỗi SQL: Đã đặt lại trạng thái phiếu mượn ID " + loanId);
                 } catch (SQLException rollbackEx) {
                     System.err.println("Lỗi nghiêm trọng khi rollback phiếu mượn do lỗi SQL: " + rollbackEx.getMessage());
                 }
            }
			return false;
		} catch (Exception e) {
             System.err.println("Lỗi không mong muốn khi trả sách: " + e.getMessage());
             e.printStackTrace();
             return false;
        }
	}

	/**
	 * Lấy danh sách các sách đang được mượn bởi một người dùng (chi tiết).
	 * @param userId ID của người dùng.
	 * @return Danh sách các LoanDetail đang hoạt động.
	 */
	public List<LoanDetail> getActiveLoansByUser(int userId) { // Đổi kiểu trả về
		try {
			return loanDao.getActiveLoansByUser(userId); // Gọi DAO đã sửa
		} catch (SQLException e) {
			System.err.println("Lỗi khi lấy sách đang mượn: " + e.getMessage());
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	/**
	 * Lấy toàn bộ lịch sử mượn/trả của một người dùng (chi tiết).
	 * @param userId ID của người dùng.
	 * @return Danh sách toàn bộ LoanDetail.
	 */
	public List<LoanDetail> getLoanHistoryByUser(int userId) { // Đổi kiểu trả về
		try {
			return loanDao.getLoanHistoryByUser(userId); // Gọi DAO đã sửa
		} catch (SQLException e) {
			System.err.println("Lỗi khi lấy lịch sử mượn: " + e.getMessage());
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	// --- Các phương thức quản lý sách cho Admin ---

	public boolean addBook(Book newBook) {
		try {
			newBook.setSoLuongConLai(newBook.getSoLuongTong());
			return bookDao.addBook(newBook);
		} catch (SQLException e) {
			System.err.println("Lỗi khi thêm sách mới: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public boolean updateBook(Book bookToUpdate) {
	    try {
	        Book currentBook = this.getBookById(bookToUpdate.getId());
	        if (currentBook == null) {
	            System.err.println("Lỗi cập nhật sách: Không tìm thấy sách với ID " + bookToUpdate.getId());
	            return false;
	        }

	        int diff = bookToUpdate.getSoLuongTong() - currentBook.getSoLuongTong();
	        int newSoLuongConLai = currentBook.getSoLuongConLai() + diff;
	        newSoLuongConLai = Math.max(0, newSoLuongConLai);
	        newSoLuongConLai = Math.min(newSoLuongConLai, bookToUpdate.getSoLuongTong());

	        int currentlyBorrowed = currentBook.getSoLuongTong() - currentBook.getSoLuongConLai();
	        if (bookToUpdate.getSoLuongTong() < currentlyBorrowed) {
	            System.err.println("Lỗi cập nhật sách: Số lượng tổng mới (" + bookToUpdate.getSoLuongTong()
	                    + ") nhỏ hơn số sách đang được mượn (" + currentlyBorrowed + ").");
	            // Có thể ném lỗi hoặc trả về thông báo cụ thể hơn
	            // throw new IllegalArgumentException("Số lượng tổng mới không hợp lệ.");
	            return false;
	        }
	        bookToUpdate.setSoLuongConLai(newSoLuongConLai);

	        return bookDao.updateBook(bookToUpdate);
	    } catch (SQLException e) {
	        System.err.println("Lỗi khi cập nhật sách: " + e.getMessage());
	        e.printStackTrace();
	        return false;
	    } catch (Exception e) { // Bắt các lỗi khác như IllegalArgumentException nếu có
	        System.err.println("Lỗi logic khi cập nhật sách: " + e.getMessage());
	        e.printStackTrace();
	        return false;
	    }
	}


	public boolean deleteBook(int bookId) throws SQLException {
		if (loanDao.countActiveLoansByBookId(bookId) > 0) {
			System.err.println("Không thể xóa sách ID " + bookId + " vì đang có người mượn.");
			throw new SQLException("Không thể xóa sách này vì đang có người mượn.", "23000");
		}
		return bookDao.deleteBook(bookId);
	}

}