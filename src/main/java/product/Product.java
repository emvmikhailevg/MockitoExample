package product;

/**
 * Товар
 *
 * @author vpyzhyanov
 * @since 16.05.2023
 */
public class Product {
    /**
     * Название
     */
    private String name;
    /**
     * Доступное количество
     */
    private int count;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    /**
     * Добавить количество товаров
     * @param count значение, на которое нужно увеличить количество товаров
     */
    public void addCount(int count) {
        this.count += count;
    }

    /**
     * Вычесть количество товаров
     * @param count значение, на которое нужно уменьшить количество товаров
     */
    public void subtractCount(int count) {
        addCount(-count);
    }
}
