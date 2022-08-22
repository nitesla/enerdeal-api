package com.enerdeal.service;

import com.enerdeal.model.GeneratorPicture;
import com.enerdeal.repo.GeneratorPictureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class GeneratorPictureService {

    @Autowired
    private GeneratorPictureRepository generatorPictureRepository;

    @Transactional
    public List<GeneratorPicture> saveBuildingPicture(List<String> imageurls, Long projectId){
        List<GeneratorPicture> pictures = new ArrayList<>();
        if(imageurls != null && !imageurls.isEmpty()){
            generatorPictureRepository.deleteAllByProjectId(projectId);
            log.info("Saving Building Picture with project Id {}", projectId);
            imageurls.forEach( imageurl -> {
                GeneratorPicture picture = new GeneratorPicture();
                picture.setImageUrl(imageurl);
                picture.setProjectId(projectId);
                GeneratorPicture save = generatorPictureRepository.save(picture);
                log.info("Building picture saved successfully ");
                pictures.add(save);
            });
        }
        return pictures;
    }

    public List<GeneratorPicture> getGeneratorPictures(long projectId){
        List<GeneratorPicture> pictures = generatorPictureRepository.findByProjectId(projectId);
        return pictures;
    }
}
