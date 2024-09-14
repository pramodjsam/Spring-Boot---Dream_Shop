package com.pramod.dreamshops.service.image;

import com.pramod.dreamshops.dto.ImageDto;
import com.pramod.dreamshops.exception.ResourceNotFoundException;
import com.pramod.dreamshops.model.Image;
import com.pramod.dreamshops.model.Product;
import com.pramod.dreamshops.repository.ImageRepository;
import com.pramod.dreamshops.service.product.IProductService;
import com.pramod.dreamshops.service.product.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ImageService implements IImageService{
    private final ImageRepository imageRepository;
    private final IProductService productService;

    public ImageService(ImageRepository imageRepository, IProductService productService) {
        this.imageRepository = imageRepository;
        this.productService = productService;
    }

    @Override
    public Image getImageById(Long id) {
        Optional<Image> image = imageRepository.findById(id);
        if (image.isPresent()) {
            return image.get();
        }else{
            throw new ResourceNotFoundException("Image not found");
        }
    }

    @Override
    public void deleteImageById(Long id) {
        Optional<Image> image = imageRepository.findById(id);
        if (image.isPresent()) {
            this.imageRepository.deleteById(id);
        }else{
            throw new ResourceNotFoundException("Image not found");
        }
    }

    @Override
    public List<ImageDto> saveImages(List<MultipartFile> files, Long productId) {
        Product product = productService.getProductById(productId);
        List<ImageDto> savedImageDto = new ArrayList<>();
        for(MultipartFile file: files){
            try{
                Image image = new Image();
                image.setFileName(file.getOriginalFilename());
                image.setFileType(file.getContentType());
                image.setImage(new SerialBlob(file.getBytes()));
                image.setProduct(product);

//                String downloadUrl = "/api/v1/images/image/download/" + image.getId();
//                image.setDownloadUrl(downloadUrl);
                Image savedImage = this.imageRepository.save(image);

                savedImage.setDownloadUrl("/api/v1/images/image/download/" + savedImage.getId());

                this.imageRepository.save(savedImage);

                ImageDto imageDto = new ImageDto();
                imageDto.setId(savedImage.getId());
                imageDto.setFileName(savedImage.getFileName());
                imageDto.setDownloadUrl(savedImage.getDownloadUrl());

                savedImageDto.add(imageDto);
            }catch(Exception e){
                throw new RuntimeException(e.getMessage());
            }
        }
        return savedImageDto;
    }

    @Override
    public void updateImage(MultipartFile file, Long imageId) {
        Optional<Image> optionalImage = imageRepository.findById(imageId);
        if (optionalImage.isPresent()) {
            Image image = optionalImage.get();
            try{
                image.setFileName(file.getOriginalFilename());
                image.setImage(new SerialBlob(file.getBytes()));
                image.setFileType(file.getContentType());
            }catch(Exception e){
                throw new RuntimeException(e.getMessage());
            }
        }else{
            throw new ResourceNotFoundException("Image not found");
        }
    }
}
