package xyz.bluspring.displayentitymodelbuilder.math

import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf
import org.joml.Vector3d
import org.joml.Vector3f

object TransformUtil {
    // https://en.wikipedia.org/wiki/Conversion_between_quaternions_and_Euler_angles
    fun toQuaternion(euler: Vector3f): Quaternionf {
        val yaw = euler.x
        val pitch = euler.y
        val roll = euler.z

        val cosRoll = Mth.cos(roll * 0.5F)
        val sinRoll = Mth.sin(roll * 0.5F)
        val cosPitch = Mth.cos(pitch * 0.5F)
        val sinPitch = Mth.sin(pitch * 0.5F)
        val cosYaw = Mth.cos(yaw * 0.5F)
        val sinYaw = Mth.sin(yaw * 0.5F)

        return Quaternionf(
            sinRoll * cosPitch * cosYaw - cosRoll * sinPitch * sinYaw,
            cosRoll * sinPitch * cosYaw + sinRoll * cosPitch * sinYaw,
            cosRoll * cosPitch * sinYaw - sinRoll * sinPitch * cosYaw,
            cosRoll * cosPitch * cosYaw + sinRoll * sinPitch * sinYaw
        )
    }

    fun mcToJoml(vec: Vec3): Vector3d {
        return Vector3d(vec.x, vec.y, vec.z)
    }

    fun jomlToMc(vec: Vector3d): Vec3 {
        return Vec3(vec.x, vec.y, vec.z)
    }

    fun jomlToMc(vec: Vector3f): Vec3 {
        return Vec3(vec.x.toDouble(), vec.y.toDouble(), vec.z.toDouble())
    }
}