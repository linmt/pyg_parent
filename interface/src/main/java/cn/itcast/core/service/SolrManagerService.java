package cn.itcast.core.service;

public interface SolrManagerService {

    public void saveItemToSolr(Long id);

    public void deleteItemFromSolr(Long id);
}
