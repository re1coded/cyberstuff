package ru.re1coded.cyberstuff.items;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.Rarity;
import org.w3c.dom.Attr;
import ru.re1coded.cyberstuff.CyberStuff;
import ru.re1coded.cyberstuff.data.ImplantDefinition;
import ru.re1coded.cyberstuff.data.ImplantRegistry;
import ru.re1coded.cyberstuff.data.ImplantSlotType;
import ru.re1coded.cyberstuff.effects.IActiveImplantEffect;
import ru.re1coded.cyberstuff.effects.IImplantEffect;

import java.util.List;

public class ModImplants {
    public static void register() {

        //frontal cortex (brain)
        //TODO: quickhacks
        /*
        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "bioconductor"),
                ImplantSlotType.BRAIN,
                false,
                List.of(
                        //allow crits for hacks
                        //grant +10% hack damage
                        //add 2 base RAM
                )
        ));

        //TODO: quickhacks
        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "cybersomatic_optimiser"),
                ImplantSlotType.BRAIN,
                true,
                List.of(
                        //allow crits for hacks
                        //grant 100% crit for one hack, cooldown
                        //grant +20% hack damage
                        //add 2 base RAM
                )
        ));
        //TODO: quickhacks
        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "camillo_ram_manager"),
                ImplantSlotType.BRAIN,
                false,
                List.of(
                        //add 2 base RAM
                        //add 1-3 RAM after you lose 90% of RAM
                )
        ));
        //TODO: quickhacks
        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "ram_reallocator"),
                ImplantSlotType.BRAIN,
                true,
                List.of(
                        //add 2 base RAM
                        //add 1-3 RAM after you lose 50% of RAM
                )
        ));
        //TODO: quickhacks
        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "ex_disk"),
                ImplantSlotType.BRAIN,
                true,
                List.of(
                        //add 4-6 base RAM
                        //speed up the hacks
                )
        ));
        */

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "kerenzikov_boost_system"),
                ImplantSlotType.BRAIN,
                true,
                List.of(
                        new IImplantEffect.ConditionalEffect(
                                Identifier.fromNamespaceAndPath(CyberStuff.MODID, "kerenzikov"),
                                new IImplantEffect.StatusEffect(
                                        MobEffects.SATURATION,
                                        2
                                )
                        )
                )
        ));

        // TODO: quickhacks

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "mechatronic_core"),
                ImplantSlotType.BRAIN,
                false,
                List.of(
                        //add 1-2 base RAM
                        //add +15% attack bonus to mech entities (iron golem, shulker, all guardians)
                )
        ));

        // TODO: quickhacks
        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "memory_boost"),
                ImplantSlotType.BRAIN,
                false,
                List.of(
                        //add 1 base RAM
                        //add 1 RAM after entity died
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "newton_module"),
                ImplantSlotType.BRAIN,
                false,
                List.of(
                        new IImplantEffect.OnKillCooldownReduceEffect(0.0135)
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "axolotl"),
                ImplantSlotType.BRAIN,
                true,
                List.of(
                        new IImplantEffect.OnKillCooldownReduceEffect(0.0135)
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "quantum_tuner"),
                ImplantSlotType.BRAIN,
                true,
                List.of(
                        new IImplantEffect.CooldownResetEffect(1200)
                )
        ));

        // TODO: quickhacks
        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "ram_upgrade"),
                ImplantSlotType.BRAIN,
                false,
                List.of(
                        //gives 1 RAM every 10-5 sec
                        //add 1 base RAM
                )
        ));

        // TODO: quickhacks
        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "self_ice"),
                ImplantSlotType.BRAIN,
                false,
                List.of(
                        //fail an enemy hack every 45 sec
                        //add 1 base RAM
                )
        ));

        //TODO: operating systems
        /*
        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "deck_arasaka"),
                ImplantSlotType.SYSTEM,
                false,
                List.of(
                        //os stuff go brrrrrrrrrr
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "deck_biotechnica_sigma"),
                ImplantSlotType.SYSTEM,
                false,
                List.of(
                        //os stuff go brrrrrrrrrr
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "deck_canto"),
                ImplantSlotType.SYSTEM,
                true,
                List.of(
                        //os stuff go brrrrrrrrrr
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "deck_paraline"),
                ImplantSlotType.SYSTEM,
                false,
                List.of(
                        //os stuff go brrrrrrrrrr
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "deck_netdiver"),
                ImplantSlotType.SYSTEM,
                true,
                List.of(
                        //os stuff go brrrrrrrrrr
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "deck_microcyber"),
                ImplantSlotType.SYSTEM,
                false,
                List.of(
                        //os stuff go brrrrrrrrrr
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "deck_rippler"),
                ImplantSlotType.SYSTEM,
                false,
                List.of(
                        //os stuff go brrrrrrrrrr
                )
        ));
        // .... and many more operating systems
        */

        // face

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "kiroshi_basic"),
                ImplantSlotType.EYES,
                false,
                List.of(
                        new IImplantEffect.CamoEffect(0)
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "kiroshi_clairvoyant"),
                ImplantSlotType.EYES,
                false,
                List.of(
                        new IActiveImplantEffect.ActiveGlowEffect(
                                12,
                                1200,
                                5,
                                entity -> entity instanceof Enemy
                        )
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "kiroshi_cockatrice"),
                ImplantSlotType.EYES,
                false,
                List.of(
                        new IImplantEffect.AttributeModifierEffect(
                                Identifier.fromNamespaceAndPath(CyberStuff.MODID, "kiroshi_cockatrice_attack_modifier"),
                                Attributes.ATTACK_DAMAGE,
                                0.25,
                                AttributeModifier.Operation.ADD_VALUE
                        )
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "kiroshi_doomsayer"),
                ImplantSlotType.EYES,
                false,
                List.of(
                        new IActiveImplantEffect.TrapDetectionEffect(
                                10,
                                5
                        )
                )
        ));

        //TODO: sentry, stalker, oracle

        // arms TODO

        // hands

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "ballistic_coprocessor"),
                ImplantSlotType.EYES,
                false,
                List.of(
                        new IImplantEffect.ArrowTrajectoryEffect(
                                40,
                                ParticleTypes.END_ROD
                        )
                )
        ));



        // blood system

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "adrenaline_booster"),
                ImplantSlotType.BLOOD_SYSTEM,
                false,
                List.of(
                        new IImplantEffect.OnNearbyDeathEffect(
                                2.0,
                                MobEffects.SATURATION,
                                1,
                                400
                        )
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "biomonitor"),
                ImplantSlotType.BLOOD_SYSTEM,
                false,
                List.of(
                        new IImplantEffect.AutoHealEffect(
                                1,
                                300
                        )
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "black_mambo"),
                ImplantSlotType.BLOOD_SYSTEM,
                false,
                List.of(
                        new IImplantEffect.OnHitEffect(
                                MobEffects.STRENGTH,
                                0,
                                120
                        )
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "blood_pump"),
                ImplantSlotType.BLOOD_SYSTEM,
                false,
                List.of(
                        new IActiveImplantEffect.ActiveToggleEffect(
                                new IImplantEffect.StatusEffect(
                                        MobEffects.HEALTH_BOOST,
                                        3
                                ),
                                600
                        )
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "heal_on_kill"),
                ImplantSlotType.BLOOD_SYSTEM,
                false,
                List.of(
                        new IImplantEffect.OnNearbyDeathEffect(
                                5,
                                MobEffects.REGENERATION,
                                0,
                                200
                        )
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "microrotors"),
                ImplantSlotType.BLOOD_SYSTEM,
                false,
                List.of(
                        new IImplantEffect.AttributeModifierEffect(
                                Identifier.fromNamespaceAndPath(CyberStuff.MODID, "microrotors_attack_speed"),
                                Attributes.ATTACK_SPEED,
                                3,
                                AttributeModifier.Operation.ADD_VALUE
                        )
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "second_heart"),
                ImplantSlotType.BLOOD_SYSTEM,
                false,
                List.of(
                        new IActiveImplantEffect.ActiveToggleEffect(
                                new IImplantEffect.LowHealthEffect(
                                        "regeneration",
                                        0.1,
                                        5,
                                        200
                                ),
                                6000
                        )

                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "threat_evac"),
                ImplantSlotType.BLOOD_SYSTEM,
                false,
                List.of(
                        new IImplantEffect.LowHealthEffect(
                                "speed",
                                0.25,
                                0,
                                500
                        )
                )
        ));

        // nerve_system

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "adrenaline_converter"),
                ImplantSlotType.BLOOD_SYSTEM,
                false,
                List.of(
                        new IImplantEffect.OnHitEffect(
                                MobEffects.SPEED,
                                0,
                                180
                        )
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "adreno_trigger"),
                ImplantSlotType.BLOOD_SYSTEM,
                true,
                List.of(
                        new IImplantEffect.OnHitEffect(
                                MobEffects.SPEED,
                                0,
                                180
                        )
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "atomic_sensors"),
                ImplantSlotType.BLOOD_SYSTEM,
                false,
                List.of(
                        new IImplantEffect.OnNearbyDeathEffect(
                                7,
                                MobEffects.SPEED,
                                0,
                                300
                        )
                )
        ));
        // TODO: kerenzikov
        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "kerenzikov"),
                ImplantSlotType.BLOOD_SYSTEM,
                false,
                List.of(
                        //slow time
                )
        ));
        // TODO: kerenzikov
        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "reflex_tuner"),
                ImplantSlotType.BLOOD_SYSTEM,
                false,
                List.of(
                        // slow time
                )
        ));

        // TODO: kerenzikov
        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "revulsor"),
                ImplantSlotType.BLOOD_SYSTEM,
                true,
                List.of(
                        // slow time
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "stabber"),
                ImplantSlotType.BLOOD_SYSTEM,
                false,
                List.of(
                        new IImplantEffect.StatusEffect(
                                MobEffects.STRENGTH,
                                0
                        )
                )
        ));
        // TODO: kerenzikov
        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "synaptic_accelerator"),
                ImplantSlotType.BLOOD_SYSTEM,
                false,
                List.of(
                        // slow time
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "tyrosine_injector"),
                ImplantSlotType.BLOOD_SYSTEM,
                false,
                List.of(
                        new IImplantEffect.OnNearbyDeathEffect(
                                3,
                                MobEffects.SPEED,
                                0,
                                120
                        ),
                        new IImplantEffect.OnNearbyDeathEffect(
                                3,
                                MobEffects.STRENGTH,
                                0,
                                120
                        )
                )
        ));

        // skin

        // TODO: carapace
        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "carapace"),
                ImplantSlotType.SKIN,
                false,
                List.of(
                        //directional attack(?)
                )
        ));
        //TODO: damage reduction
        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "cellular_adapter"),
                ImplantSlotType.SKIN,
                false,
                List.of(
                        new IImplantEffect.AttributeModifierEffect(
                                Identifier.fromNamespaceAndPath(CyberStuff.MODID, "cellular_adapter_armor"),
                                Attributes.ARMOR,
                                7.5,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        new IImplantEffect.DamageReductionEffect(
                                0.1,
                                DamageTypes.EXPLOSION
                        ),
                        new IImplantEffect.DamageReductionEffect(
                                0.2,
                                DamageTypes.MACE_SMASH
                        )
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "chitin"),
                ImplantSlotType.SKIN,
                true,
                List.of(
                        new IImplantEffect.StatusEffect(
                                MobEffects.HEALTH_BOOST,
                                0
                        ),
                        new IImplantEffect.StatusEffect(
                                MobEffects.ABSORPTION,
                                0
                        ),
                        new IImplantEffect.AttributeModifierEffect(
                                Identifier.fromNamespaceAndPath(CyberStuff.MODID, "chitin_armor"),
                                Attributes.ARMOR,
                                8.0,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        new IImplantEffect.AttributeModifierEffect(
                                Identifier.fromNamespaceAndPath(CyberStuff.MODID, "chitin_toughness"),
                                Attributes.ARMOR_TOUGHNESS,
                                3.0,
                                AttributeModifier.Operation.ADD_VALUE
                        )
                )
        ));
        // TODO: quickhacks
        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "cogito_lattice"),
                ImplantSlotType.SKIN,
                false,
                List.of(
                        new IImplantEffect.AttributeModifierEffect(
                                Identifier.fromNamespaceAndPath(CyberStuff.MODID, "lattice_base_armor"),
                                Attributes.ARMOR,
                                0.5,
                                AttributeModifier.Operation.ADD_VALUE
                        )
                        // add armor if RAM below 50%
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "nano_plating"),
                ImplantSlotType.SKIN,
                false,
                List.of(
                        new IImplantEffect.AttributeModifierEffect(
                                Identifier.fromNamespaceAndPath(CyberStuff.MODID, "nano_plating_armor"),
                                Attributes.ARMOR,
                                1.0,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        new IImplantEffect.ProjectileDeflectEffect(0.04)

                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "optical_camo"),
                ImplantSlotType.SKIN,
                false,
                List.of(
                        new IImplantEffect.AttributeModifierEffect(
                                Identifier.fromNamespaceAndPath(CyberStuff.MODID, "camo_armor"),
                                Attributes.ARMOR,
                                0.5,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        new IImplantEffect.AttributeModifierEffect(
                                Identifier.fromNamespaceAndPath(CyberStuff.MODID, "camo_armor"),
                                Attributes.SNEAKING_SPEED,
                                2.0,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        new IActiveImplantEffect.ActiveToggleEffect(
                                new IImplantEffect.CamoEffect(0),
                                200
                        )
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "pain_editor"),
                ImplantSlotType.SKIN,
                false,
                List.of(
                        new IImplantEffect.AttributeModifierEffect(
                                Identifier.fromNamespaceAndPath(CyberStuff.MODID, "pain_editor_armor"),
                                Attributes.ARMOR,
                                6.0,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        new IImplantEffect.DamageReductionEffect(0.06, null)
                )
        ));

        // TODO: damage over time
        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "painducer"),
                ImplantSlotType.SKIN,
                false,
                List.of(
                        new IImplantEffect.AttributeModifierEffect(
                                Identifier.fromNamespaceAndPath(CyberStuff.MODID, "painducer_armor"),
                                Attributes.ARMOR,
                                6.0,
                                AttributeModifier.Operation.ADD_VALUE
                        )
                        // add armor if RAM below 50%
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "proxishield"),
                ImplantSlotType.SKIN,
                false,
                List.of(
                        new IImplantEffect.AttributeModifierEffect(
                                Identifier.fromNamespaceAndPath(CyberStuff.MODID, "proxishield_base_armor"),
                                Attributes.ARMOR,
                                0.5,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        new IImplantEffect.DistanceDamageReductionEffect(0.2, 3, true)
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "peripheral_inverse"),
                ImplantSlotType.SKIN,
                true,
                List.of(
                        new IImplantEffect.AttributeModifierEffect(
                                Identifier.fromNamespaceAndPath(CyberStuff.MODID, "peripheral_inverse_base_armor"),
                                Attributes.ARMOR,
                                0.5,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        new IImplantEffect.DistanceDamageReductionEffect(0.45, 3, true)
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "rangeguard"),
                ImplantSlotType.SKIN,
                false,
                List.of(
                        new IImplantEffect.AttributeModifierEffect(
                                Identifier.fromNamespaceAndPath(CyberStuff.MODID, "rangeguard_base_armor"),
                                Attributes.ARMOR,
                                0.5,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        new IImplantEffect.DistanceDamageReductionEffect(0.35, 6, false)
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "shock_n_awe"),
                ImplantSlotType.SKIN,
                false,
                List.of(
                        new IImplantEffect.AttributeModifierEffect(
                                Identifier.fromNamespaceAndPath(CyberStuff.MODID, "shock_n_awe_base_armor"),
                                Attributes.ARMOR,
                                1.0,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        new IImplantEffect.ElectricShockEffect(
                                6.0,
                                0,
                                300
                        )
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "subdermal_armor"),
                ImplantSlotType.SKIN,
                false,
                List.of(
                        new IImplantEffect.AttributeModifierEffect(
                                Identifier.fromNamespaceAndPath(CyberStuff.MODID, "subdermal_armor"),
                                Attributes.ARMOR,
                                4.0,
                                AttributeModifier.Operation.ADD_VALUE
                        )
                )
        ));

        // skeleton

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "bionic_joints"),
                ImplantSlotType.SKELETON,
                false,
                List.of(
                        new IImplantEffect.AttributeModifierEffect(
                                Identifier.fromNamespaceAndPath(CyberStuff.MODID, "bionic_joints_armor"),
                                Attributes.ARMOR,
                                1.0,
                                AttributeModifier.Operation.ADD_VALUE
                        )
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "dense_marrow"),
                ImplantSlotType.SKELETON,
                false,
                List.of(
                        new IImplantEffect.AttributeModifierEffect(
                                Identifier.fromNamespaceAndPath(CyberStuff.MODID, "dense_marrow_attack_damage"),
                                Attributes.ATTACK_DAMAGE,
                                2.0,
                                AttributeModifier.Operation.ADD_VALUE
                        )
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "epimorphic_skeleton"),
                ImplantSlotType.SKELETON,
                false,
                List.of(
                        new IImplantEffect.AttributeModifierEffect(
                                Identifier.fromNamespaceAndPath(CyberStuff.MODID, "epimorphic_skeleton_armor"),
                                Attributes.ARMOR,
                                6.0,
                                AttributeModifier.Operation.ADD_VALUE
                        ),

                        new IImplantEffect.AttributeModifierEffect(
                                Identifier.fromNamespaceAndPath(CyberStuff.MODID, "epimorphic_skeleton_health_boost"),
                                Attributes.MAX_HEALTH,
                                1.0,
                                AttributeModifier.Operation.ADD_VALUE
                        )
                )
        ));
        //TODO: quickhacks
        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "feen_x"),
                ImplantSlotType.SKELETON,
                false,
                List.of(
                        //add ram regen rate
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "para_bellum"),
                ImplantSlotType.SKELETON,
                false,
                List.of(
                        new IImplantEffect.AttributeModifierEffect(
                                Identifier.fromNamespaceAndPath(CyberStuff.MODID, "para_bellum_armor"),
                                Attributes.ARMOR,
                                5.0,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        new IImplantEffect.AttributeModifierEffect(
                                Identifier.fromNamespaceAndPath(CyberStuff.MODID, "para_bellum_toughness"),
                                Attributes.ARMOR_TOUGHNESS,
                                0.1,
                                AttributeModifier.Operation.ADD_VALUE
                        )
                )
        ));
        //TODO: quickhacks
        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "ram_recoup"),
                ImplantSlotType.SKELETON,
                false,
                List.of(
                        //regen ram by incoming damage
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "rara_avis"),
                ImplantSlotType.SKELETON,
                true,
                List.of(
                        new IImplantEffect.AttributeModifierEffect(
                                Identifier.fromNamespaceAndPath(CyberStuff.MODID, "rara_avis_toughness"),
                                Attributes.ARMOR_TOUGHNESS,
                                0.3,
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                        )
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "scar_coalescer"),
                ImplantSlotType.SKELETON,
                false,
                List.of(
                        new IImplantEffect.LowHealthEffect(
                                "regeneration",
                                0.5,
                                0,
                                500
                        )
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "scarab"),
                ImplantSlotType.SKELETON,
                false,
                List.of(
                        new IImplantEffect.CrouchBonusEffect(
                                Identifier.fromNamespaceAndPath(CyberStuff.MODID, "scarab_armor"),
                                6.0,
                                0
                        ),
                        new IImplantEffect.AttributeModifierEffect(
                                Identifier.fromNamespaceAndPath(CyberStuff.MODID, "scarab_base_armor"),
                                Attributes.ARMOR,
                                0.5,
                                AttributeModifier.Operation.ADD_VALUE
                        )
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "universal_booster"),
                ImplantSlotType.SKELETON,
                false,
                List.of(
                        new IImplantEffect.LowHealthEffect(
                                "regeneration",
                                0.5,
                                0,
                                500
                        )
                )
        ));

        //legs

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "fortified_ankles"),
                ImplantSlotType.LEGS,
                false,
                List.of(
                        new IImplantEffect.CrouchBonusEffect(
                                Identifier.fromNamespaceAndPath(CyberStuff.MODID, "fortified_ankles_jump_boost"),
                                0,
                                0
                        )
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "jenkins_tendons"),
                ImplantSlotType.LEGS,
                false,
                List.of(
                        new IImplantEffect.StatusEffect(
                                MobEffects.SPEED,
                                0
                        )
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "leeroy_ligament_system"),
                ImplantSlotType.LEGS,
                true,
                List.of(
                        new IImplantEffect.StatusEffect(
                                MobEffects.SPEED,
                                2
                        )
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "lynx_paws"),
                ImplantSlotType.LEGS,
                false,
                List.of(
                        new IImplantEffect.CrouchBonusEffect(
                                Identifier.fromNamespaceAndPath(CyberStuff.MODID, "lynx_paws_sneak"),
                                0,
                                0
                        ),
                        new IImplantEffect.AttributeModifierEffect(
                                Identifier.fromNamespaceAndPath(CyberStuff.MODID, "lynx_paws_damage_negation"),
                                Attributes.FALL_DAMAGE_MULTIPLIER,
                                -0.15,
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                        )
                )
        ));
        // TODO: double jump
        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "reinforced_tendons"),
                ImplantSlotType.LEGS,
                false,
                List.of(
                        new IImplantEffect.StatusEffect(
                                MobEffects.JUMP_BOOST,
                                0
                        )
                        // ideally need double jump
                )
        ));
    }
}
