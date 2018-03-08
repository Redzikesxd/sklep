package pl.tomek.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import pl.tomek.model.Product;
import pl.tomek.model.Zdjecia;
import pl.tomek.repository.ProductRepository;
import pl.tomek.repository.ZdjeciaRepositoru;

import javax.persistence.Id;
import javax.validation.Valid;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller
public class MyController {
    private ProductRepository productRepository;
    private ZdjeciaRepositoru zdjeciaRepositoru;

    @Autowired
    public void setZdjeciaRepositoru(ZdjeciaRepositoru zdjeciaRepositoru) {
        this.zdjeciaRepositoru = zdjeciaRepositoru;
    }

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    @GetMapping("/my")
    public String my(Model model, @RequestParam(defaultValue = "0") int page) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username
        model.addAttribute("username", name);
        model.addAttribute("nieznajomy", "anonymousUser");


        Page<Product> all = productRepository.findByOwner(name, new PageRequest(page, 10));
        int ile = productRepository.findByOwner(name).size();

        if (ile % 10 == 0) {
            ile = ile / 10;
        } else {
            ile = ile / 10 + 1;
        }
        int tab[] = new int[ile];
        for (int i = 0; i < ile; i++) {
            tab[i] = i;

        }
        model.addAttribute("ile", tab);
        model.addAttribute("products", all);

        return "myForm";
    }


    @GetMapping("/usun")
    public String usun(@RequestParam Long ID) {
        productRepository.delete(ID);
        return "redirect:my";
    }

    @GetMapping("/details")
    public String detail(@RequestParam Long ID, Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username
        model.addAttribute("username", name);
        model.addAttribute("nieznajomy", "anonymousUser");

        Product product = productRepository.findOne(ID);
        List<Zdjecia> zdjecia = product.getZdjecia();
        Set<String> zd = new HashSet<>();
        for (Zdjecia z : zdjecia) {
            zd.add(z.getAdres());
        }
        model.addAttribute("zdjecia", zd);
        model.addAttribute("product", product);
        return "myDetails";
    }

    @GetMapping("/edytuj")
    public String edytuj(@RequestParam Long ID, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username
        model.addAttribute("username", name);
        model.addAttribute("nieznajomy", "anonymousUser");
        Product product = productRepository.findOne(ID);
        model.addAttribute("ID", ID);
        model.addAttribute("product", product);
        List<Zdjecia> zdjecia = product.getZdjecia();
        Set<String> zd = new HashSet<>();
        for (Zdjecia z : zdjecia) {
            zd.add(z.getAdres());
        }
        model.addAttribute("zdjecia", zd);
        return "EditDetailsForm";
    }


    @PostMapping("/edytuj")
    public String edytuj(@RequestParam Long ID, Model model, @Valid @ModelAttribute Product product, BindingResult bindingResult, @RequestParam("plik[]") MultipartFile[] file) {
        int size = file.length;
        if (file[0] != null) {
            if (file.length > 9) {
                model.addAttribute("limit", "Limit zdjec to 9");
                return "redirect:edytuj?ID=" + ID;
            }
            for (int i = 0; i < file.length; i++) {
                String images = file[i].getContentType();

                images = images.substring(0, images.indexOf('/'));

                if (images.equals("image")) {
                } else if (images.equals("application")) {
                    size--;
                } else {
                    model.addAttribute("badExtend", "Moga byc tylko zdjecia");
                    return "redirect:edytuj?ID=" + ID;
                }
            }
        }
        if (size >= 1) {
            product.getZdjecia().clear();
        }

        if (size >= 1 && !bindingResult.hasErrors()) {
            for (int i = 0; i < file.length; i++) {
                try {
                    String extend = file[i].getOriginalFilename();
                    extend = extend.substring(extend.indexOf('.'));

                    UUID uuid = UUID.randomUUID();
                    String filename = "src\\main\\resources\\static\\images\\products\\" + uuid.toString() + extend;
                    byte[] bytes = file[i].getBytes();
                    File files = new File(filename);

                    files.createNewFile();
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(files));
                    bufferedOutputStream.write(bytes);
                    bufferedOutputStream.close();
                    Zdjecia zdjecia = new Zdjecia();
                    zdjecia.setAdres("images/products/" + uuid.toString() + extend);
                    zdjeciaRepositoru.save(zdjecia);
                    product.getZdjecia().add(zdjecia);


                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }


        if (bindingResult.hasErrors()) {
            return "redirect:edytuj?ID=" + ID;
        } else {
            Product tmp = productRepository.findOne(ID);
            product.setLicytujacy(tmp.getLicytujacy());
            product.setOwner(tmp.getOwner());
            product.setID(product.getID() - 1);

            productRepository.delete(ID);
            System.out.println(product.getZdjecia());
            product.setZdjecia(product.getZdjecia());
            ID = productRepository.save(product).getID();
            Product product1 = productRepository.findOne(ID);
            System.out.println("Produkt po dodania" + product1.getZdjecia());

        }

        return "redirect:details?ID=" + ID;
    }
}