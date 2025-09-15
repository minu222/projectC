package bookstore;

public class Bookstore {




    public static void main(String[] args) {
        Book book, book1, book2, book3;

        book = new Book("축구의 역사", 1000);
        book1 = new Book("축구 아는 여자", 2000);
        book2 = new Book("축구의 이해", 3000);
        book3 = new Book("골프 바이블", 4000);

        book.showPrice();
        book1.showPrice();
        book2.showPrice();
        book3.showPrice();

        Customer customer, customer1;

        customer = new Customer("박지성", "810101-1111111", "영국 맨체스터", "000 - 5000 - 0001");
        customer1 = new Customer("김연아", "900101-2222222", "대한민국 서울", "000 - 6000 - 0001");


        System.out.println("이름 \t 주민번호 \t \t 주소 \t \t 핸드폰");
        customer.customerList();
        customer1.customerList();


    }

}
