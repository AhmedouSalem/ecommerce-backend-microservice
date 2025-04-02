package com.aryan.productservice.model;

import com.aryan.productservice.dto.CategoryResponse;
import com.aryan.productservice.dto.ProductDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@Builder(toBuilder = true)
@NoArgsConstructor
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long price;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Lob
    @Column(columnDefinition = "mediumblob")        // current limit : 16MB____use longblob for 4gb size (postgres)
    private byte[] img;

    private Long categoryId;

    /** On doit l'ajouter pour y encapsuler les données de la catégories **/
    @Transient
    private CategoryResponse category;
    /** L'annotation Transiant veut dire à Hibernate d'ignorer category au niveau de la BD **/




    //	TODO  : add mapstruct
    public ProductDto getDto() {
        return ProductDto.builder()
                .id(this.id)  // Ensure the ID is being correctly assigned here
                .name(this.name)
                .price(this.price)
                .description(this.description)
                .byteImg(this.img)
                .categoryId(this.categoryId)
                .categoryName(this.category.getName()) // Ajouter le nom de la categorie
                .build();
    }


}
