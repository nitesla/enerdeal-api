package com.enerdeal.service;

import com.enerdeal.model.BuildingPicture;
import com.enerdeal.repo.BuildingPictureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BuildingPictureService {
    @Autowired
    BuildingPictureRepository buildingPictureRepository;
    @Transactional
    public List<BuildingPicture> saveBuildingPicture(List<String> imageurls, Long projectId){
        List<BuildingPicture> pictures = new ArrayList<>();
        if(imageurls != null && !imageurls.isEmpty()){
            buildingPictureRepository.deleteAllByProjectId(projectId);
            log.info("Saving Building Picture with project Id {}", projectId);
            imageurls.forEach(imageurl -> {
                BuildingPicture picture = new BuildingPicture();
                picture.setImageUrl(imageurl);
                picture.setProjectId(projectId);
                BuildingPicture save = buildingPictureRepository.save(picture);
                log.info("Building picture saved successfully ");
                pictures.add(save);
            });
        }
        return pictures;
    }

    public List<BuildingPicture> getBuildingPictures(long projectId){
        List<BuildingPicture> pictures = buildingPictureRepository.findByProjectId(projectId);
        return pictures;
    }
}
