package com.example.pillyohae.order.service;

//@SpringBootTest
//class OrderServiceTest {
//    @Autowired
//    private OrderService orderService;
//    @Autowired
//    private OrderRepository orderRepository;
//    @Autowired
//    private ProductRepository productRepository;
//    @Autowired
//    private UserRepository userRepository;
//
//    private String email;
//    private ShippingAddress address;
//    @Autowired
//    private CategoryRepository categoryRepository;
//
//    @BeforeEach
//    void setUp() {
//        email = "Test@tester.com";
//        address = new ShippingAddress("TestUser","010-0000-0000","test-zip","test-road","100-100");
//        User testUser = new User("TestUser", email, "password123", address, Role.SELLER);
//        Category category = new Category(1L,"testCategory");
//        Nutrient nutrient = new Nutrient("Nutrient","Nutrient");
//        Product product = new Product(testUser,"product1",category ,"test1", "test1", 10000L, 100 , nutrient);
//        userRepository.save(testUser);
//        productRepository.save(product);
//    }
//
//    @Test
//    void createOrderByProductsWithNoCouponTest() {
//        OrderCreateRequestDto.ProductOrderInfo productOrderInfo = new OrderCreateRequestDto.ProductOrderInfo(1L, 3);
//        Long productId = 1L;
//        List<OrderCreateRequestDto.ProductOrderInfo> productOrderInfoList = new ArrayList<>();
//        productOrderInfoList.add(productOrderInfo);
//        OrderCreateRequestDto orderCreateRequestDto = new OrderCreateRequestDto(productOrderInfoList, null);
//        orderService.createOrderByProducts(email, orderCreateRequestDto);
//        // 주문 생성
//        OrderDetailResponseDto responseDto = orderService.createOrderByProducts(email, orderCreateRequestDto);
//
//        // 주문이 성공적으로 생성되었는지 검증
//        assertNotNull(responseDto.getOrderInfo());
//        assertNotNull(responseDto.getOrderInfo().getOrderName());
//        // 주문 상세 정보 검증
//        Order order = orderRepository.findById(responseDto.getOrderInfo().getOrderId()).orElseThrow();
//        assertEquals(email, order.getUser().getEmail());
//        assertEquals(1, order.getOrderProducts().size());
//        assertEquals(3, order.getOrderProducts().get(0).getQuantity());
//    }
//}
