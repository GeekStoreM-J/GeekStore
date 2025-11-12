package com.geekstore.geekstore.Service;

import com.geekstore.geekstore.Model.Product;
import com.geekstore.geekstore.Repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile; // Import necessário
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public static final String ENDERECO_ARMAZENAMENTO_ARQUIVO = "/home/byuku/projetos/GeekStore/src/main/resources/static/images/"; // <---
    // MODIFIQUE
    // AQUI

    public String saveImageUrl(MultipartFile arquivo) throws IOException {

        if (arquivo == null || arquivo.isEmpty()) {
            // Retorna null ou throw new NullPointerException, dependendo da sua regra de
            // negócio
            return null;
        }

        // Constrói o caminho completo onde o arquivo será salvo no servidor
        var enderecoArquivo = new File(
                ENDERECO_ARMAZENAMENTO_ARQUIVO + File.separator + arquivo.getOriginalFilename());

        String caminhoDestino = enderecoArquivo.getCanonicalPath();
        String caminhoPermitido = new File(ENDERECO_ARMAZENAMENTO_ARQUIVO).getCanonicalPath();

        if (!caminhoDestino.startsWith(caminhoPermitido)) {
            throw new SecurityException("Nome do arquivo não é viável!");
        }

        // Cria o arquivo físico, substituindo se já existir
        Files.copy(
                arquivo.getInputStream(),
                enderecoArquivo.toPath(),
                StandardCopyOption.REPLACE_EXISTING);

        // Retorna o caminho RELATIVO ao diretório static/ que o navegador irá
        // requisitar
        String enderecoWeb = "images/" + arquivo.getOriginalFilename();
        return enderecoWeb;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }
}