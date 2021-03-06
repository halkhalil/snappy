/*
 * Copyright 2017 Josue Gontijo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.joshworks.snappy.extensions.ssr;


/**
 * Created by Josue on 12/07/2016.
 */
public class Instance {

    private String id;
    private String address;
    private String hostname;
    private int port = 80;
    private long lastUpdate;
    private long since;
    private long downSince;
    private String name;
    private State state = State.DOWN;
    private boolean useHostname;
    private boolean discoverable;
    private boolean fetchServices;

    public String getId() {
        return id;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String resolveAddress() {
        return (useHostname ? hostname : address) + ":" + port;
    }

    public long getSince() {
        return since;
    }

    public void setSince(long since) {
        this.since = since;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public long getDownSince() {
        return downSince;
    }

    public void setDownSince(long downSince) {
        this.downSince = downSince;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDiscoverable() {
        return discoverable;
    }

    public void setDiscoverable(boolean discoverable) {
        this.discoverable = discoverable;
    }

    public boolean isFetchServices() {
        return fetchServices;
    }

    public void setFetchServices(boolean fetchServices) {
        this.fetchServices = fetchServices;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public boolean isUseHostname() {
        return useHostname;
    }

    public void setUseHostname(boolean useHostname) {
        this.useHostname = useHostname;
    }

    public void updateInstanceState(Instance.State newState) {
        state = newState;
        if (Instance.State.DOWN.equals(newState)) {
            downSince = System.currentTimeMillis();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Instance instance = (Instance) o;

        if (port != instance.port) return false;
        return address != null ? address.equals(instance.address) : instance.address == null;
    }

    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + port;
        return result;
    }

    @Override
    public String toString() {
        return "Instance{" +
                "id='" + id + '\'' +
                ", address='" + resolveAddress() + '\'' +
                ", lastUpdate=" + lastUpdate +
                ", since=" + since +
                ", downSince=" + downSince +
                ", name='" + name + '\'' +
                ", state=" + state +
                ", discoverable=" + discoverable +
                ", fetchServices=" + fetchServices +
                '}';
    }

    public enum State {
        UP, DOWN, OUT_OF_SERVICE
    }
}
