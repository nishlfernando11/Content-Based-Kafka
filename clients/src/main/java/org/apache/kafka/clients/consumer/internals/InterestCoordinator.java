package org.apache.kafka.clients.consumer.internals;

import org.apache.kafka.clients.ClientResponse;
import org.apache.kafka.clients.Metadata;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.protocol.ApiKeys;
import org.apache.kafka.common.requests.FetchRequest;
import org.apache.kafka.common.requests.SendInterestsRequest;
import org.apache.kafka.common.requests.SendInterestsResponse;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.utils.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created with IntelliJ IDEA.
 * User: Irosha
 */

public class InterestCoordinator {

    private static final Logger log = LoggerFactory.getLogger(InterestCoordinator.class);

    private final ConsumerNetworkClient client;
    private final String groupId;
    private final Time time;
    private final Metadata metadata;
    private final SubscriptionState subscriptions;


    public InterestCoordinator(ConsumerNetworkClient client,
                               String groupId,
                               Time time,
                               Metadata metadata,
                               SubscriptionState subscriptions){

        this.client = client;
        this.groupId = groupId;
        this.time = time;
        this.metadata = metadata;
        this.subscriptions = subscriptions;
    }

    public void setInterests(String interests){
        this.metadata.setInterests(interests);
    }

    public String getInterests(){
        return this.metadata.getInterests();
    }

    /**
     * Set-up a clientInterests request for any node that we have assigned partitions
     */
    public void sendInterests() {

        for (Map.Entry<Node, SendInterestsRequest> entry : createSendInterestsRequests().entrySet()) {
            final SendInterestsRequest request = entry.getValue();
            final Node target = entry.getKey();

            client.send(target, ApiKeys.SEND_INTERESTS, request)
                    .addListener(new RequestFutureListener<ClientResponse>() {
                        @Override
                        public void onSuccess(ClientResponse resp) {
                            SendInterestsResponse response = (SendInterestsResponse) resp.responseBody();
                            log.info("Send interest request success!");
                        }

                        @Override
                        public void onFailure(RuntimeException e) {
                            log.debug("Send interests request to {} failed", target, e);
                        }
                    });
        }
    }


    /**
     * Create sendInterests requests for all nodes for which we have assigned partitions
     */
    private Map<Node, SendInterestsRequest> createSendInterestsRequests() {
        // create the info
        Cluster cluster = metadata.fetch();
        Map<Node, SendInterestsRequest> requests = new HashMap<>();
        for (TopicPartition partition : fetchablePartitions()) {
            Node node = cluster.leaderFor(partition);
            if (node == null) {
                metadata.requestUpdate();
            } else if (this.client.pendingRequestCount(node) == 0) {
                // if there is a leader and no in-flight requests, create the fetches
                SendInterestsRequest interests = new SendInterestsRequest(this.groupId, this.metadata.getInterests());
                requests.put(node, interests);
                log.trace("Added send interests request for partition {}", partition);
            } else {
                log.trace("Skipping send interests for partition {} because there is an in-flight request to {}", partition, node);
            }
        }
        return requests;
    }

    private List<TopicPartition> fetchablePartitions() {
        List<TopicPartition> fetchable = subscriptions.fetchablePartitions();
        return fetchable;
    }
}
