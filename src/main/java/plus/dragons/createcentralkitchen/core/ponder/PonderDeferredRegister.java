package plus.dragons.createcentralkitchen.core.ponder;

import com.simibubi.create.foundation.ponder.PonderStoryBoardEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.List;

public class PonderDeferredRegister {
    boolean registered = false;
    final String namespace;
    private final List<PonderRegistryObject> objects = new ArrayList<>();
    
    public static PonderDeferredRegister create(String namespace) {
        return new PonderDeferredRegister(namespace);
    }
    
    private PonderDeferredRegister(String namespace) {
        this.namespace = namespace;
    }
    
    public PonderRegistryObject create(String schematic,
                                       PonderStoryBoardEntry.PonderStoryBoard storyBoard) {
        Validate.isTrue(!registered, "Cannot create new PonderRegistryObject with a registered PonderDeferredRegister");
        var object = new PonderRegistryObject(this, new ResourceLocation(namespace, schematic), storyBoard);
        objects.add(object);
        return object;
    }
    
    public void register(IEventBus bus) {
        bus.register(new EventDispatcher(this));
    }
    
    /**
     * For internal use and datagen only, don't call this in production environment!
     */
    public void registerInternal() {
        objects.forEach(PonderRegistryObject::register);
    }
    
    public static class EventDispatcher {
        private final PonderDeferredRegister register;
        
        public EventDispatcher(final PonderDeferredRegister register) {
            this.register = register;
        }
        
        //Set it to EventPriority.LOWEST so PonderRegistryObjects can be initialized before registered
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public void handleEvent(final FMLClientSetupEvent event) {
            event.enqueueWork(register::registerInternal);
        }
        
    }
    
}
