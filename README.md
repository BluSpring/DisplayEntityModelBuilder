# DisplayEntityModelBuilder

A mod I wrote to help with creating custom models with Item Display Entities in 1.19.4.
Originally written for [Vandal Events'](https://vandal.events) Spring 2023 mod Atrophy, however
I never finished working on it.

The format is fairly easy to understand, as long as you have a resource pack to support it.

Here's a sample JSON:
```json
{
    "id": "e:nihachu",
    "items": {
        "head": {
            "id": "minecraft:bone",
            "model": 55
        },
        "torso": {
            "id": "minecraft:bone",
            "model": 56
        },
        "right_arm": {
            "id": "minecraft:bone",
            "model": 57
        },
        "left_arm": {
            "id": "minecraft:bone",
            "model": 58
        },
        "right_leg": {
            "id": "minecraft:bone",
            "model": 59
        },
        "left_leg": {
            "id": "minecraft:bone",
            "model": 60
        }
    },
    "shapes": [
        {
            "id": "torso",
            "position": [
                0,
                14,
                0
            ],
            "rotation": [
                0, -10, 0
            ],
            "origin": [
                0,
                12,
                0
            ],
            "children": [
                {
                    "id": "head",
                    "position": [
                        0,
                        12,
                        0
                    ],
                    "origin": [
                        0,
                        24,
                        0
                    ],
                    "rotation": [0, -7.5, 2.5]
                },
                {
                    "id": "right_arm",
                    "position": [
                        3,
                        -1,
                        0
                    ],
                    "origin": [
                        -6,
                        22,
                        0
                    ],
                    "rotation": [-135, -2, -9]
                },
                {
                    "id": "left_arm",
                    "position": [
                        0,
                        4,
                        0
                    ],
                    "origin": [
                        6,
                        22,
                        0
                    ],
                    "rotation": [7.28, 0.26, 16.5]
                }
            ]
        },
        {
            "id": "left_leg",
            "position": [
                0,
                2,
                0
            ],
            "origin": [
                2,
                12,
                0
            ],
            "rotation": [3.2, 6.8, -65]
        },
        {
            "id": "right_leg",
            "position": [
                0,
                2,
                0
            ],
            "origin": [
                -2,
                12,
                0
            ],
            "rotation": [-3.2, -6.8, -65]
        }
    ],
    "hitboxes": []
}
```

i won't bother making a tutorial, feel free to figure this out yourself.

**NOTE**: If you're using Blockbench, the rotations are in ZYX format!