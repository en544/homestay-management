package com.example.homestay.config;

import com.example.homestay.domain.BookingOrder;
import com.example.homestay.domain.Homestay;
import com.example.homestay.domain.OrderStatus;
import com.example.homestay.domain.Review;
import com.example.homestay.domain.Room;
import com.example.homestay.domain.RoomStatus;
import com.example.homestay.repository.BookingOrderRepository;
import com.example.homestay.repository.HomestayRepository;
import com.example.homestay.repository.ReviewRepository;
import com.example.homestay.repository.RoomRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDemoData(
            HomestayRepository homestayRepository,
            RoomRepository roomRepository,
            BookingOrderRepository orderRepository,
            ReviewRepository reviewRepository) {
        return args -> {
            if (homestayRepository.count() > 0) {
                return;
            }

            Homestay moganshan = homestay(
                    "云栖山居",
                    "湖州",
                    "浙江省湖州市德清县莫干山镇仙潭村",
                    "藏在竹海里的山居小院，推窗即见云与山。",
                    "云栖山居由老宅改建，保留木梁与青石院落的质感。清晨可在露台看云海，午后沿竹林步道散步，夜晚围炉煮茶。门店距离莫干山风景区换乘中心约 12 分钟车程。",
                    "/images/stay-1.jpg",
                    "竹海景观,山野早餐,围炉煮茶");

            Homestay dali = homestay(
                    "洱海听风",
                    "大理",
                    "云南省大理市双廊镇大建旁村",
                    "面朝洱海的白色庭院，把日落留在落地窗前。",
                    "洱海听风位于双廊临水一侧，公共露台正对苍山洱海。客房以木、棉麻和浅色石材为主，提供环海骑行地图与本地早餐。适合慢旅行、亲子和摄影度假。",
                    "/images/stay-2.jpg",
                    "一线海景,日落露台,环海骑行");

            Homestay wuyuan = homestay(
                    "青岚小院",
                    "上饶",
                    "江西省上饶市婺源县篁岭景区山下",
                    "白墙黛瓦旁的安静小院，春看花田，秋晒丰收。",
                    "青岚小院坐落于篁岭山脚，保留徽派民居的白墙黛瓦与木格窗。院内有茶室和开放式厨房，步行可达乡间步道。春季可看油菜花田，秋季可体验晒秋与手作。",
                    "/images/stay-3.jpg",
                    "徽派庭院,茶室,田园徒步");

            homestayRepository.saveAll(List.of(moganshan, dali, wuyuan));

            Room mountainKing = room(moganshan, "听竹·山景大床房", "大床房", 2,
                    "688.00", 5, RoomStatus.AVAILABLE, "地暖,观景露台,双人早餐", "/images/room-1.jpg");
            Room mountainFamily = room(moganshan, "枕云·亲子套房", "亲子套房", 4,
                    "1088.00", 2, RoomStatus.AVAILABLE, "儿童帐篷,浴缸,地暖", "/images/room-2.jpg");
            Room lakeView = room(dali, "临风·海景大床房", "大床房", 2,
                    "898.00", 4, RoomStatus.AVAILABLE, "落地窗,浴缸,海景露台", "/images/room-3.jpg");
            Room lakeTwin = room(dali, "拾光·海景双床房", "双床房", 2,
                    "828.00", 3, RoomStatus.CLEANING, "落地窗,智能马桶,双人早餐", "/images/room-4.jpg");
            Room courtyard = room(wuyuan, "青瓦·庭院双床房", "双床房", 2,
                    "568.00", 6, RoomStatus.AVAILABLE, "独立庭院,茶席,徽式早餐", "/images/room-5.jpg");
            Room loft = room(wuyuan, "晒秋·阁楼家庭房", "家庭房", 3,
                    "768.00", 2, RoomStatus.MAINTENANCE, "阁楼天窗,儿童床,茶室", "/images/room-6.jpg");

            roomRepository.saveAll(List.of(
                    mountainKing, mountainFamily, lakeView, lakeTwin, courtyard, loft));

            LocalDate today = LocalDate.now();
            BookingOrder arrival = order(
                    "陈知夏", "13800138001", mountainKing,
                    today, today.plusDays(2), 1, 2, OrderStatus.CONFIRMED,
                    "晚间八点左右到店，希望安排安静一些的房间。");
            BookingOrder staying = order(
                    "周亦舟", "13900139002", lakeView,
                    today.minusDays(1), today.plusDays(2), 1, 2, OrderStatus.CHECKED_IN,
                    "需要一份双人早餐。");
            BookingOrder pending = order(
                    "林小满", "13700137003", courtyard,
                    today.plusDays(3), today.plusDays(5), 2, 4, OrderStatus.PENDING,
                    "两间房尽量相邻。");
            BookingOrder completed = order(
                    "许言", "13600136004", mountainFamily,
                    today.minusDays(8), today.minusDays(6), 1, 3, OrderStatus.COMPLETED,
                    "带一名儿童入住。");
            BookingOrder cancelled = order(
                    "沈清", "13500135005", lakeTwin,
                    today.plusDays(6), today.plusDays(8), 1, 1, OrderStatus.CANCELLED,
                    "行程调整。");
            orderRepository.saveAll(List.of(arrival, staying, pending, completed, cancelled));

            Review review = new Review();
            review.setOrder(completed);
            review.setRating(5);
            review.setContent("院子很安静，亲子房空间比照片里更舒服。早餐是现做的，管家还帮我们规划了竹林徒步路线。");
            review.setReply("谢谢你们的喜欢，期待下次再一起看山里的云。");
            reviewRepository.save(review);
        };
    }

    private Homestay homestay(
            String name,
            String city,
            String address,
            String summary,
            String description,
            String coverImage,
            String tags) {
        Homestay homestay = new Homestay();
        homestay.setName(name);
        homestay.setCity(city);
        homestay.setAddress(address);
        homestay.setSummary(summary);
        homestay.setDescription(description);
        homestay.setCoverImage(coverImage);
        homestay.setTags(tags);
        homestay.setActive(true);
        return homestay;
    }

    private Room room(
            Homestay homestay,
            String name,
            String roomType,
            int capacity,
            String price,
            int totalRooms,
            RoomStatus status,
            String amenities,
            String coverImage) {
        Room room = new Room();
        room.setHomestay(homestay);
        room.setName(name);
        room.setRoomType(roomType);
        room.setCapacity(capacity);
        room.setPrice(new BigDecimal(price));
        room.setTotalRooms(totalRooms);
        room.setStatus(status);
        room.setAmenities(amenities);
        room.setCoverImage(coverImage);
        return room;
    }

    private BookingOrder order(
            String guestName,
            String phone,
            Room room,
            LocalDate checkIn,
            LocalDate checkOut,
            int roomCount,
            int guests,
            OrderStatus status,
            String note) {
        BookingOrder order = new BookingOrder();
        order.setOrderNo("HS" + System.nanoTime());
        order.setGuestName(guestName);
        order.setGuestPhone(phone);
        order.setRoom(room);
        order.setCheckIn(checkIn);
        order.setCheckOut(checkOut);
        order.setRoomCount(roomCount);
        order.setGuests(guests);
        order.setTotalAmount(room.getPrice().multiply(
                BigDecimal.valueOf(checkOut.toEpochDay() - checkIn.toEpochDay())
                        .multiply(BigDecimal.valueOf(roomCount))));
        order.setStatus(status);
        order.setNote(note);
        return order;
    }
}
