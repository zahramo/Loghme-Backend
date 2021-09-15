package ir.ac.ut.ece.ie.loghme.repository.scheduler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.order.OrderMapper;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.user.UserMapper;
import ir.ac.ut.ece.ie.loghme.repository.exception.RestaurantNotFound;
import ir.ac.ut.ece.ie.loghme.repository.exception.RestaurantOutOfRegion;
import ir.ac.ut.ece.ie.loghme.repository.model.*;
import ir.ac.ut.ece.ie.loghme.repository.http.*;

import java.sql.SQLException;
import java.util.*;

public class DeliverOrder extends TimerTask {
    private Order order;
    private ArrayList<Courier> couriers;

    public DeliverOrder(Order order){
        this.order = order;
        couriers = new ArrayList<>();
    }

    @Override
    public void run() {
        LoghmeHttpClient httpClient = new LoghmeHttpClient();
        try {
            System.out.println("Send Http GET request for Couriers Data");
            try {
                httpClient.sendGet("http://138.197.181.131:8080/deliveries");
            } finally {
                httpClient.close();
            }
            ObjectMapper mapper = new ObjectMapper();
            couriers = (ArrayList<Courier>) mapper.readValue(httpClient.getResponseData(), new TypeReference<List<Courier>>() {});
            LoghmeRepository.getCurInstance().addCouriersToDB(couriers);
            if(couriers.size() != 0){
                cancel();
                allocateProperCourier();
                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new SendOrder(),0,1000);
            }
        } catch (Exception e) {}
    }

    public void allocateProperCourier() throws RestaurantOutOfRegion, RestaurantNotFound, SQLException {
        Location restaurantLocation = LoghmeRepository.getCurInstance().
                                            getRestaurant(order.getRestaurantId()).getLocation();
        Double minimumDeliveryTime = Double.POSITIVE_INFINITY;
        User user = UserMapper.getInstance().find(new ArrayList<>(Arrays.asList(order.getUserId())));
        Courier deliverer = null;
        for(Courier courier:couriers){
            Double distanceCtoR = Math.sqrt( (Math.pow(courier.getLocation().getX() -
                    restaurantLocation.getX(),2)) + (Math.pow(courier.getLocation().getY() - restaurantLocation.getY(),2)));
            Double distanceRtoU = Math.sqrt( (Math.pow(restaurantLocation.getX() -
                    user.getLocation().getX(),2)) + (Math.pow(restaurantLocation.getY() - user.getLocation().getY(),2)));
            Double deliveryTime = (distanceCtoR + distanceRtoU)/courier.getVelocity();
            if(deliveryTime < minimumDeliveryTime){
                minimumDeliveryTime = deliveryTime;
                deliverer = courier;
            }
        }

        order.setDeliveryTime((int) Math.ceil(minimumDeliveryTime*60));
        order.setCourierId(deliverer.getId());
        order.setState(State.CourierOnTheWay);

//        System.out.println(deliverer.getId() + " : deliver Id ");
        ArrayList<String> ids = new ArrayList<>();
        ids.add(order.getUserId());
        ids.add(order.getId());
        OrderMapper.getInstance().updateCourierId(ids,deliverer.getId());
        OrderMapper.getInstance().updateDeliveryTime(ids,(int) Math.ceil(minimumDeliveryTime*60));
        OrderMapper.getInstance().updateState(ids,State.CourierOnTheWay.toString());

        System.out.println("In Deliver Order Module for " + order.getId() + " :");
        System.out.println("updated");

    }

    public class SendOrder extends TimerTask{
        @Override
        public void run() {
            order.setDeliveryTime(order.getDeliveryTime() - 1);
            if(order.getDeliveryTime() <= 0){
                cancel();
                order.setState(State.Delievered);
                ArrayList<String> ids = new ArrayList<>();
                ids.add(order.getUserId());
                ids.add(order.getId());
                try {
                    OrderMapper.getInstance().updateState(ids,State.Delievered.toString());
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
