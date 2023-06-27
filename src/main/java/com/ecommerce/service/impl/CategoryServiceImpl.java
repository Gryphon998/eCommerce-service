package com.ecommerce.service.impl;

import com.ecommerce.common.ServerResponse;
import com.ecommerce.dao.CategoryMapper;
import com.ecommerce.pojo.Category;
import com.ecommerce.service.ICategoryService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    private final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServerResponse<String> addCategory(String categoryName, Integer parentId) {
        if (StringUtils.isNotBlank(categoryName) && parentId != null) {
            Category category = new Category();
            category.setParentId(parentId);
            category.setName(categoryName);
            category.setStatus(true);
            int insertResult = categoryMapper.insert(category);
            if (insertResult > 0) {
                return ServerResponse.createBySuccessMessage("Category added successfully");
            } else {
                return ServerResponse.createByErrorMessage("Failed to add category");
            }
        }
        return ServerResponse.createByErrorMessage("Invalid parameters");
    }

    @Override
    public ServerResponse<String> updateCategoryInfo(String categoryName, Integer categoryId) {
        if (StringUtils.isNotBlank(categoryName) && categoryId != null) {
            Category category = new Category();
            category.setId(categoryId);
            category.setName(categoryName);
            int updateResult = categoryMapper.updateByPrimaryKeySelective(category);
            if (updateResult > 0) {
                return ServerResponse.createBySuccessMessage("Category information updated successfully");
            } else {
                return ServerResponse.createByErrorMessage("Failed to update category information");
            }
        }
        return ServerResponse.createByErrorMessage("Invalid parameters");
    }

    @Override
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer parentId) {
        if (parentId != null) {
            List<Category> categories = categoryMapper.selectByParentId(parentId);
            if (CollectionUtils.isEmpty(categories)) {
                logger.info("No child categories found for the current category");
            } else {
                return ServerResponse.createBySuccess(categories);
            }
        }
        return ServerResponse.createByErrorMessage("Invalid parameters");
    }

    /**
     * Recursive method to query the id of the current node and the id of its child nodes
     *
     * @param categoryId
     * @return
     */
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId) {
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categorySet, categoryId);


        List<Integer> categoryIdList = Lists.newArrayList();
        if (categoryId != null) {
            for (Category categoryItem : categorySet) {
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }


    // Recursive algorithm to find child nodes
    private Set<Category> findChildCategory(Set<Category> categorySet, Integer categoryId) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null) {
            categorySet.add(category);
        }
        // Find child nodes, recursive algorithms must have an exit condition
        List<Category> categoryList = categoryMapper.selectByParentId(categoryId);
        for (Category categoryItem : categoryList) {
            findChildCategory(categorySet, categoryItem.getId());
        }
        return categorySet;
    }
}
