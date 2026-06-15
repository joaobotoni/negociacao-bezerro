package com.omni.negociacaobezerros.data.source.network.models;

import java.util.List;

public final class NetworkRoutes {

    public static final class Request {
        public final Origin origin;
        public final Destination destination;
        public final String travelMode;
        public final String routingPreference;
        public final String units;

        public Request(Origin origin, Destination destination) {
            this.origin = origin;
            this.destination = destination;
            this.travelMode = "DRIVE";
            this.routingPreference = "TRAFFIC_AWARE_OPTIMAL";
            this.units = "METRIC";
        }

        public static final class Origin {
            public final Location location;

            public Origin(Location location) {
                this.location = location;
            }
        }

        public static final class Destination {
            public final Location location;

            public Destination(Location location) {
                this.location = location;
            }
        }

        public static final class Location {
            public final LatLng latLng;

            public Location(LatLng latLng) {
                this.latLng = latLng;
            }
        }

        public static final class LatLng {
            public final double latitude;
            public final double longitude;

            public LatLng(double latitude, double longitude) {
                this.latitude = latitude;
                this.longitude = longitude;
            }
        }
    }

    public static final class Response {
        public final List<Route> routes;

        public Response(List<Route> routes) {
            this.routes = routes;
        }

        public static final class Route {
            public final int distanceMeters;

            public Route(int distanceMeters) {
                this.distanceMeters = distanceMeters;
            }
        }
    }
}
