package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@ComponentScan
public class ItemValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Item.class.isAssignableFrom(clazz);
        // Item == clazz
        // Item == subItem
    }

    @Override
    public void validate(Object target, Errors errors) {
        Item item = (Item) target;

        // 검증 로직
        if (!StringUtils.hasText(item.getItemName())) {
            errors.rejectValue("itemName", "required");
        }
        if (null == item.getPrice() || 1000 > item.getPrice() || 1000000 < item.getPrice()) {
            errors.rejectValue("price", "range", new Object[]{1000, 2000000}, "가격은 1000에서 1000000 까지 허용");
        }
        if (null == item.getQuantity() || 9999 <= item.getQuantity()) {
            errors.rejectValue("quantity", "max", new Object[]{5000}, "수량은 최대 9999까지 허용");
        }
        // 특정 필드가 아닌 복합 Rule 검증
        if (null != item.getPrice() && null != item.getQuantity()) {
            int resultPrice = item.getPrice() * item.getQuantity();
            errors.reject("totalPriceMin", new Object[]{20000}, "가격 * 수량 합은 10000원 이상 필요, 현재 값 =" + resultPrice);
        }
    }
}
