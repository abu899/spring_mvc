package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
@Slf4j
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }

//    @PostMapping("/add")
    public String addItemV1(@ModelAttribute Item item,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes,
                          Model model) {

        // 검증 로직
        if (!StringUtils.hasText(item.getItemName())) {
//            errors.put("itemName", "상품 이름은 필수!");
            bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수!"));
        }
        if (null == item.getPrice() || 1000 > item.getPrice() || 1000000 < item.getPrice()) {
//            errors.put("price", "가격은 1000에서 1000000 까지 허용");
            bindingResult.addError(new FieldError("item", "price", "가격은 1000에서 1000000 까지 허용"));
        }
        if (null == item.getQuantity() || 9999 <= item.getQuantity()) {
//            errors.put("quantity", "수량은 최대 9999까지 허용");
            bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9999까지 허용"));
        }
        // 특정 필드가 아닌 복합 Rule 검증
        if (null != item.getPrice() && null != item.getQuantity()) {
            int resultPrice = item.getPrice() * item.getQuantity();
//            if (10000 > resultPrice) {
//                errors.put("globalError", "가격 * 수량 합은 10000원 이상 필요, 현재 값 =" + resultPrice);
//            }
            bindingResult.addError(new ObjectError("item", "가격 * 수량 합은 10000원 이상 필요, 현재 값 =" + resultPrice));
        }
        // 검증 실패시 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
//            model.addAttribute("errors", errors);
            // 자동으로 bindingResult는 view로 넘어간다
            log.info("error={}", bindingResult);
            return "validation/v2/addForm";
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @PostMapping("/add")
    public String addItemV2(@ModelAttribute Item item,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes,
                            Model model) {

        // 검증 로직
        if (!StringUtils.hasText(item.getItemName())) {
//            errors.put("itemName", "상품 이름은 필수!");
//            bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수!"));
            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false,
                    null, null,"상품 이름은 필수!"));
        }
        if (null == item.getPrice() || 1000 > item.getPrice() || 1000000 < item.getPrice()) {
//            errors.put("price", "가격은 1000에서 1000000 까지 허용");
//            bindingResult.addError(new FieldError("item", "price", "가격은 1000에서 1000000 까지 허용"));
            bindingResult.addError(new FieldError("item", "price", item.getPrice(), false,
                    null, null,"가격은 1000에서 1000000 까지 허용"));
        }
        if (null == item.getQuantity() || 9999 <= item.getQuantity()) {
//            errors.put("quantity", "수량은 최대 9999까지 허용");
//            bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9999까지 허용"));
            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(), false,
                    null, null,"수량은 최대 9999까지 허용"));
        }
        // 특정 필드가 아닌 복합 Rule 검증
        if (null != item.getPrice() && null != item.getQuantity()) {
            int resultPrice = item.getPrice() * item.getQuantity();
//            if (10000 > resultPrice) {
//                errors.put("globalError", "가격 * 수량 합은 10000원 이상 필요, 현재 값 =" + resultPrice);
//            }
            bindingResult.addError(new ObjectError("item", null, null,
                    "가격 * 수량 합은 10000원 이상 필요, 현재 값 =" + resultPrice));
        }
        // 검증 실패시 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
//            model.addAttribute("errors", errors);
            // 자동으로 bindingResult는 view로 넘어간다
            log.info("error={}", bindingResult);
            return "validation/v2/addForm";
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

}

