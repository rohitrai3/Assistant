#include "audio.h"
#include <QPermission>
#include <QAudioSource>
#include <QMediaDevices>

Audio::Audio(QObject *parent)
    : QObject{parent}
{}

void Audio::onClicked()
{
    const QList<QAudioDevice> audioDevices = QMediaDevices::audioInputs();
    for (const QAudioDevice &device: audioDevices) {
        qInfo() << "ID: " << device.id() << Qt::endl;
        qInfo() << "Description: " << device.description() << Qt::endl;
        qInfo() << "Is default: " << (device.isDefault() ? "Yes" :"No") << Qt::endl;
    }
}
