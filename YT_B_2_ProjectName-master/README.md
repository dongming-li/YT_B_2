[![pipeline status](https://git.linux.iastate.edu/309Fall2017/YT_B_2_ProjectName/badges/master/pipeline.svg)](https://git.linux.iastate.edu/309Fall2017/YT_B_2_ProjectName/commits/master)
[![coverage report](https://git.linux.iastate.edu/309Fall2017/YT_B_2_ProjectName/badges/master/coverage.svg)](https://git.linux.iastate.edu/309Fall2017/YT_B_2_ProjectName/commits/master)

# CySwapper

[Server Readme](server/README.md)

## Getting the Source

```sh
git clone --recursive git@git.linux.iastate.edu:309Fall2017/YT_B_2_ProjectName.git
cd YT_B_2_ProjectName
```

**Note**: if `git clone --recursive` does not work, *(as it was introduced in git version 1.6.5)*, you may need to use the following commands instead:

```sh
git clone git@git.linux.iastate.edu:309Fall2017/YT_B_2_ProjectName.git
cd YT_B_2_ProjectName
git submodule update --init --recursive
```