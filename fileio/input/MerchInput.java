package fileio.input;

import commands.jsonReader.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class MerchInput {
    private String name;
    private Integer price;
    private String description;

    public MerchInput(final Command command) {
        this.name = command.getName();
        this.price = command.getPrice();
        this.description = command.getDescription();
    }
}
