package com.enerdeal.service;

import com.enerdeal.model.EquipmentNameplatePicture;
import com.enerdeal.repo.EquipmentNameplatePictureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class EquipmentNameplatePictureService {
    @Autowired
    EquipmentNameplatePictureRepository equipmentNameplatePicture;

    @Transactional
    public List<EquipmentNameplatePicture> saveEquipmentNameplatePicture(List<String> imageurls, Long projectId){
        List<EquipmentNameplatePicture> pictures = new ArrayList<>();
        if(imageurls != null && !imageurls.isEmpty()){
            equipmentNameplatePicture.deleteAllByProjectId(projectId);
            log.info("Saving Building Picture with project Id {}", projectId);
            imageurls.forEach(imageurl -> {
                EquipmentNameplatePicture picture = new EquipmentNameplatePicture();
                picture.setImageUrl(imageurl);
                picture.setProjectId(projectId);
                EquipmentNameplatePicture save = equipmentNameplatePicture.save(picture);
                log.info("Building picture saved successfully ");
                pictures.add(save);
            });
        }
        return pictures;
    }

    public List<EquipmentNameplatePicture> getEquipmentNameplatePictures(long projectId){
        List<EquipmentNameplatePicture> pictures = equipmentNameplatePicture.findByProjectId(projectId);
        return pictures;
    }
}
