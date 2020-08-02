package cn.itcast.core.listener;

import cn.itcast.core.service.SolrManagerService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;

public class ItemSearchListener implements MessageListener {

    @Autowired
    private SolrManagerService solrManagerService;

    @Override
    public void onMessage(Message message) {
        //为了方便获取文本消息, 将原生的消息对象转换成activeMq的文本消息对象
        ActiveMQTextMessage atm = (ActiveMQTextMessage)message;

        try {
            String goodsId = atm.getText();
            solrManagerService.saveItemToSolr(Long.parseLong(goodsId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
