package ru.re1coded.cyberstuff;

import net.neoforged.fml.common.EventBusSubscriber;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static ru.re1coded.cyberstuff.CyberStuff.MODID;

@EventBusSubscriber(modid = MODID)
public class CyberStuffDatagen {

    static void main(String[] args) throws IOException {
        Path resourcesPath = Paths.get("src", "main", "resources", "assets", MODID);
        Path modelsPath = resourcesPath.resolve("models/item");
        Path itemsPath  = resourcesPath.resolve("items");

        Files.createDirectories(modelsPath);
        Files.createDirectories(itemsPath);

        for (String name : IMPLANTS) {
            writeFlatModel(modelsPath, name);
            System.out.println("✓ " + name);
        }

        writeImplantSelector(itemsPath);
        System.out.printf("%nГотово! Сгенерировано %d имплантов.%n", IMPLANTS.size());
    }

    private static void writeFlatModel(Path dir, String name) throws IOException {
        String json = """
        {
          "parent": "minecraft:item/generated",
          "textures": {
            "layer0": "%s:item/%s"
          }
        }
        """.formatted(MODID, name);
        Files.writeString(dir.resolve(name + "_flat.json"), json);
    }

    private static void writeImplantSelector(Path itemsPath) throws IOException {
        StringBuilder handCases = new StringBuilder();
        StringBuilder flatCases = new StringBuilder();

        for (int i = 0; i < IMPLANTS.size(); i++) {
            String name = IMPLANTS.get(i);
            String comma = i < IMPLANTS.size() - 1 ? "," : "";

            handCases.append("""
              {
                "when": "%s",
                "model": {
                  "type": "minecraft:model",
                  "model": "%s:misc/implant_box_hpex",
                  "textures": {
                    "implant_texture": "%s:item/%s"
                  }
                }
              }%s
            """.formatted(name, MODID, MODID, name, comma));

            flatCases.append("""
              {
                "when": "%s",
                "model": {
                  "type": "minecraft:model",
                  "model": "%s:item/%s_flat"
                }
              }%s
            """.formatted(name, MODID, name, comma));
        }

        String json = """
        {
          "model": {
            "type": "minecraft:select",
            "property": "minecraft:display_context",
            "cases": [
              {
                "when": ["firstperson_righthand", "firstperson_lefthand",
                         "thirdperson_righthand", "thirdperson_lefthand",
                         "ground"],
                "model": {
                  "type": "minecraft:select",
                  "property": "minecraft:custom_model_data",
                  "index": 0,
                  "cases": [
        %s
                  ],
                  "fallback": {
                    "type": "minecraft:model",
                    "model": "minecraft:item/barrier"
                  }
                }
              }
            ],
            "fallback": {
              "type": "minecraft:select",
              "property": "minecraft:custom_model_data",
              "index": 0,
              "cases": [
        %s
              ],
              "fallback": {
                "type": "minecraft:model",
                "model": "minecraft:item/barrier"
              }
            }
          }
        }
        """.formatted(handCases, flatCases);

        Files.writeString(itemsPath.resolve("implant.json"), json);
    }

    private static final List<String> IMPLANTS = List.of(
            "kerenzikov_boost_system",
            "mechatronic_core",
            "memory_boost",
            "newton_module",
            "axolotl",
            "quantum_tuner",
            "ram_upgrade",
            "self_ice",
            "kiroshi_basic",
            "kiroshi_clairvoyant",
            "kiroshi_cockatrice",
            "kiroshi_doomsayer",
            "ballistic_coprocessor",
            "adrenaline_booster",
            "biomonitor",
            "black_mambo",
            "blood_pump",
            "heal_on_kill",
            "microrotors",
            "second_heart",
            "threat_evac",
            "adrenaline_converter",
            "adreno_trigger",
            "atomic_sensors",
            "kerenzikov",
            "reflex_tuner",
            "revulsor",
            "stabber",
            "synaptic_accelerator",
            "tyrosine_injector",
            "carapace",
            "cellular_adapter",
            "chitin",
            "cogito_lattice",
            "nano_plating",
            "optical_camo",
            "pain_editor",
            "painducer",
            "proxishield",
            "peripheral_inverse",
            "rangeguard",
            "shock_n_awe",
            "subdermal_armor",
            "bionic_joints",
            "dense_marrow",
            "epimorphic_skeleton",
            "feen_x",
            "para_bellum",
            "ram_recoup",
            "rara_avis",
            "scar_coalescer",
            "scarab",
            "universal_booster",
            "fortified_ankles",
            "jenkins_tendons",
            "leeroy_ligament_system",
            "lynx_paws",
            "reinforced_tendons"
    );
}
