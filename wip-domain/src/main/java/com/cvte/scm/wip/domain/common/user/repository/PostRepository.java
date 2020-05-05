package com.cvte.scm.wip.domain.common.user.repository;

import com.cvte.scm.wip.domain.common.user.entity.PostEntity;
import com.cvte.scm.wip.domain.common.user.entity.UserEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/5 11:35
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public interface PostRepository {

    List<UserEntity> getUserListByPostId(String postId);

    PostEntity getPost(String id);

    UserEntity getUserByObjectTypeAndId(String postId, String userId);

}
